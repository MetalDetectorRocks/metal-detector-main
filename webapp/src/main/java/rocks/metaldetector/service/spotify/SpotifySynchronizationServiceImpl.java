package rocks.metaldetector.service.spotify;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
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
import static rocks.metaldetector.service.spotify.SpotifyFetchType.ARTISTS;

@Service
@AllArgsConstructor
public class SpotifySynchronizationServiceImpl implements SpotifySynchronizationService {

  private final SpotifyService spotifyService;
  private final FollowArtistService followArtistService;

  @Override
  public int synchronizeArtists(List<String> artistsIds) {
    return followArtistService.followSpotifyArtists(artistsIds);
  }

  @Override
  public List<SpotifyArtistDto> fetchSavedArtists(List<SpotifyFetchType> fetchTypes) {
    Set<SpotifyArtistDto> savedArtists = new HashSet<>();

    if (fetchTypes.contains(ALBUMS)) {
      savedArtists.addAll(getArtistsFromLikedAlbums());
    }
    if (fetchTypes.contains(ARTISTS)) {
      savedArtists.addAll(getFollowedArtists());
    }

    return savedArtists.stream()
        .filter(artist -> !followArtistService.isCurrentUserFollowing(artist.getId(), SPOTIFY))
        .sorted(Comparator.comparing(SpotifyArtistDto::getName))
        .collect(Collectors.toList());
  }

  private List<SpotifyArtistDto> getFollowedArtists() {
    return spotifyService.fetchFollowedArtists();
  }

  private List<SpotifyArtistDto> getArtistsFromLikedAlbums() {
    List<SpotifyAlbumDto> likedAlbums = spotifyService.fetchLikedAlbums();
    List<String> artistIds = likedAlbums.stream()
            .flatMap(album -> album.getArtists().stream())
            .distinct()
            .map(SpotifyArtistDto::getId)
            .collect(Collectors.toList());

    return spotifyService.searchArtistsByIds(artistIds);
  }
}
