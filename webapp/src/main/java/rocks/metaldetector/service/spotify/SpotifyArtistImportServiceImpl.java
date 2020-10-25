package rocks.metaldetector.service.spotify;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import rocks.metaldetector.persistence.domain.user.UserEntity;
import rocks.metaldetector.persistence.domain.user.UserRepository;
import rocks.metaldetector.security.CurrentPublicUserIdSupplier;
import rocks.metaldetector.service.SlicingService;
import rocks.metaldetector.service.artist.ArtistDto;
import rocks.metaldetector.service.artist.ArtistService;
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

  @Override
  public List<ArtistDto> importArtistsFromLikedReleases() {
    String publicUserId = currentPublicUserIdSupplier.get();
    UserEntity currentUser = userRepository.findByPublicId(publicUserId).orElseThrow(
        () -> new ResourceNotFoundException("User with public id '" + publicUserId + "' not found!")
    );

    List<SpotifyAlbumDto> importedAlbums = spotifyService.importAlbums(currentUser.getSpotifyAuthorization().getAccessToken());
    List<String> artistIds = importedAlbums.stream()
        .flatMap(album -> album.getArtists().stream())
        .map(SpotifyArtistDto::getId)
        .distinct()
        .collect(Collectors.toList());

    List<String> newArtistsIds = artistService.findNewArtistIds(artistIds);

    return persistAndReturnNewArtists(newArtistsIds).stream()
        .filter(artist -> !followArtistService.isCurrentUserFollowing(artist.getExternalId(), SPOTIFY))
        .peek(artist -> followArtistService.follow(artist.getExternalId(), SPOTIFY))
        .collect(Collectors.toList());
  }

  private List<ArtistDto> persistAndReturnNewArtists(List<String> newArtistsIds) {
    List<SpotifyArtistDto> spotifyArtistDtos = new ArrayList<>();
    int totalPages = (int) Math.ceil((double) newArtistsIds.size() / (double) PAGE_SIZE);
    for (int i = 1; i <= totalPages; i++) {
      List<String> idsPerPage = slicingService.slice(newArtistsIds, i, PAGE_SIZE);
      spotifyArtistDtos.addAll(spotifyService.searchArtistsByIds(idsPerPage));
    }
    return artistService.persistAndReturn(spotifyArtistDtos);
  }
}
