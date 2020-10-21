package rocks.metaldetector.service.spotify;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import rocks.metaldetector.persistence.domain.artist.ArtistEntity;
import rocks.metaldetector.persistence.domain.artist.ArtistRepository;
import rocks.metaldetector.persistence.domain.user.UserEntity;
import rocks.metaldetector.persistence.domain.user.UserRepository;
import rocks.metaldetector.security.CurrentPublicUserIdSupplier;
import rocks.metaldetector.service.SlicingService;
import rocks.metaldetector.service.artist.ArtistDto;
import rocks.metaldetector.service.artist.ArtistService;
import rocks.metaldetector.service.artist.ArtistTransformer;
import rocks.metaldetector.service.artist.FollowArtistService;
import rocks.metaldetector.spotify.facade.SpotifyService;
import rocks.metaldetector.spotify.facade.dto.SpotifyAlbumDto;
import rocks.metaldetector.spotify.facade.dto.SpotifyArtistDto;
import rocks.metaldetector.support.exceptions.ResourceNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static rocks.metaldetector.persistence.domain.artist.ArtistSource.SPOTIFY;

@Service
@AllArgsConstructor
public class SpotifyArtistImportServiceImpl implements SpotifyArtistImportService {

  static final int PAGE_SIZE = 50;

  private final SpotifyService spotifyService;
  private final CurrentPublicUserIdSupplier currentPublicUserIdSupplier;
  private final UserRepository userRepository;
  private final FollowArtistService followArtistService;
  private final ArtistService artistService;
  private final SlicingService slicingService;
  private final ArtistRepository artistRepository;
  private final ArtistTransformer artistTransformer;

  @Override
  public List<ArtistDto> importArtists() {
    String publicUserId = currentPublicUserIdSupplier.get();
    UserEntity currentUser = userRepository.findByPublicId(publicUserId).orElseThrow(
        () -> new ResourceNotFoundException("User with public id '" + publicUserId + "' not found!")
    );

    List<SpotifyAlbumDto> importedAlbums = spotifyService.importAlbums(currentUser.getSpotifyAuthorization().getAccessToken());
    List<String> newArtistsIds = findNewArtistIds(importedAlbums);

    return persistNewArtists(newArtistsIds).stream()
        .peek(artist -> followArtistService.follow(artist.getExternalId(), SPOTIFY))
        .collect(Collectors.toList());
  }

  private List<String> findNewArtistIds(List<SpotifyAlbumDto> importedAlbums) {
    return importedAlbums.stream()
        .flatMap(album -> album.getArtists().stream())
        .map(SpotifyArtistDto::getId)
        .distinct()
        .filter(artistId -> !artistService.existsArtistByExternalId(artistId, SPOTIFY))
        .collect(Collectors.toList());
  }

  private List<ArtistDto> persistNewArtists(List<String> newArtistsIds) {
    List<SpotifyArtistDto> artistDtos = new ArrayList<>();
    int totalPages = newArtistsIds.size() % PAGE_SIZE == 0 ? newArtistsIds.size() / PAGE_SIZE
                                                           : newArtistsIds.size() / PAGE_SIZE + 1;
    for (int i = 1; i <= totalPages; i++) {
      List<String> idsPerPage = slicingService.slice(newArtistsIds, i, PAGE_SIZE);
      artistDtos.addAll(spotifyService.searchArtistsByIds(idsPerPage));
    }

    List<ArtistEntity> artistEntities = artistDtos.stream()
        .map(artistDto -> new ArtistEntity(artistDto.getId(), artistDto.getName(), artistDto.getImageUrl(), SPOTIFY))
        .collect(Collectors.toList());
    artistRepository.saveAll(artistEntities);

    return artistEntities.stream()
        .map(artistTransformer::transform)
        .collect(Collectors.toList());
  }
}
