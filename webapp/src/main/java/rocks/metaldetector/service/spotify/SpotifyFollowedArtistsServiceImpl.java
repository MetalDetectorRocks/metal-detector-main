package rocks.metaldetector.service.spotify;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import rocks.metaldetector.service.artist.ArtistDto;
import rocks.metaldetector.service.artist.ArtistDtoTransformer;
import rocks.metaldetector.service.artist.ArtistService;
import rocks.metaldetector.service.artist.FollowArtistService;
import rocks.metaldetector.spotify.facade.SpotifyService;
import rocks.metaldetector.spotify.facade.dto.SpotifyAlbumDto;
import rocks.metaldetector.spotify.facade.dto.SpotifyArtistDto;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static rocks.metaldetector.persistence.domain.artist.ArtistSource.SPOTIFY;

@Service
@AllArgsConstructor
public class SpotifyFollowedArtistsServiceImpl implements SpotifyFollowedArtistsService {

  static final String ARTIST_FETCH_TYPE_ALBUM = "album";

  private final SpotifyService spotifyService;
  private final FollowArtistService followArtistService;
  private final ArtistService artistService;
  private final SpotifyUserAuthorizationService userAuthorizationService;
  private final ArtistDtoTransformer artistDtoTransformer;

  @Override
  public List<ArtistDto> importArtistsFromLikedReleases() {
    String spotifyAccessToken = userAuthorizationService.getOrRefreshToken();

    List<SpotifyAlbumDto> importedAlbums = spotifyService.fetchLikedAlbums(spotifyAccessToken);
    List<String> artistIds = importedAlbums.stream()
        .flatMap(album -> album.getArtists().stream())
        .map(SpotifyArtistDto::getId)
        .distinct()
        .collect(Collectors.toList());

    List<String> newArtistsIds = artistService.findNewArtistIds(artistIds);
    persistNewArtists(newArtistsIds);

    return artistService.findAllArtistsByExternalIds(artistIds).stream()
        .filter(artist -> !followArtistService.isCurrentUserFollowing(artist.getExternalId(), SPOTIFY))
        .peek(artist -> followArtistService.follow(artist.getExternalId(), SPOTIFY))
        .collect(Collectors.toList());
  }

  @Override
  public List<ArtistDto> getNewFollowedArtists(List<String> fetchTypes) {
   Set<ArtistDto> artistDtos = new HashSet<>();

    for (String fetchType : fetchTypes) {
      if (fetchType.equalsIgnoreCase(ARTIST_FETCH_TYPE_ALBUM)) {
        artistDtos.addAll(getArtistsFromLikedAlbums());
      }
    }

    return artistDtos.stream()
        .filter(artist -> !followArtistService.isCurrentUserFollowing(artist.getExternalId(), SPOTIFY))
        .collect(Collectors.toList());
  }

  private void persistNewArtists(List<String> newArtistsIds) {
    List<SpotifyArtistDto> spotifyArtistDtos = spotifyService.searchArtistsByIds(newArtistsIds);
    artistService.persistArtists(spotifyArtistDtos);
  }

  private List<ArtistDto> getArtistsFromLikedAlbums() {
    List<SpotifyAlbumDto> likedAlbums = spotifyService.fetchLikedAlbums(userAuthorizationService.getOrRefreshToken());
    return likedAlbums.stream()
        .flatMap(album -> album.getArtists().stream())
        .distinct()
        .map(artistDtoTransformer::transformSpotifyArtistDto)
        .collect(Collectors.toList());
  }
}
