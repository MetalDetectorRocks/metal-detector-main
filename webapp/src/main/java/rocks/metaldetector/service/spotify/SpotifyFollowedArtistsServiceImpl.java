package rocks.metaldetector.service.spotify;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import rocks.metaldetector.service.artist.ArtistDto;
import rocks.metaldetector.service.artist.ArtistService;
import rocks.metaldetector.service.artist.FollowArtistService;
import rocks.metaldetector.spotify.facade.SpotifyService;
import rocks.metaldetector.spotify.facade.dto.SpotifyAlbumDto;
import rocks.metaldetector.spotify.facade.dto.SpotifyArtistDto;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static rocks.metaldetector.persistence.domain.artist.ArtistSource.SPOTIFY;
import static rocks.metaldetector.service.spotify.SpotifyFetchType.ALBUMS;

@Service
@AllArgsConstructor
public class SpotifyFollowedArtistsServiceImpl implements SpotifyFollowedArtistsService {

  private final SpotifyService spotifyService;
  private final FollowArtistService followArtistService;
  private final ArtistService artistService;
  private final SpotifyUserAuthorizationService userAuthorizationService;

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
        .collect(Collectors.toList());
  }

  @Override
  public List<SpotifyArtistDto> getNewFollowedArtists(List<SpotifyFetchType> fetchTypes) {
    Set<SpotifyArtistDto> artistDtos = new HashSet<>();

    if (fetchTypes.contains(ALBUMS)) {
      artistDtos.addAll(getArtistsFromLikedAlbums());
    }

    return artistDtos.stream()
        .filter(artist -> !followArtistService.isCurrentUserFollowing(artist.getId(), SPOTIFY))
        .sorted(Comparator.comparing(SpotifyArtistDto::getName))
        .collect(Collectors.toList());
  }

  private void persistNewArtists(List<String> newArtistsIds) {
    List<SpotifyArtistDto> spotifyArtistDtos = spotifyService.searchArtistsByIds(newArtistsIds);
    artistService.persistArtists(spotifyArtistDtos);
  }

  private List<SpotifyArtistDto> getArtistsFromLikedAlbums() {
    List<SpotifyAlbumDto> likedAlbums = spotifyService.fetchLikedAlbums(userAuthorizationService.getOrRefreshToken());
    List<String> artistIds = likedAlbums.stream()
            .flatMap(album -> album.getArtists().stream())
            .distinct()
            .map(SpotifyArtistDto::getId)
            .collect(Collectors.toList());

    return spotifyService.searchArtistsByIds(artistIds);
  }
}
