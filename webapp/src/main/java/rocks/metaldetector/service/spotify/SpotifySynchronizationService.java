package rocks.metaldetector.service.spotify;

import rocks.metaldetector.spotify.facade.dto.SpotifyArtistDto;

import java.util.List;

public interface SpotifySynchronizationService {

  int synchronizeArtists(List<String> artistIds);

  List<SpotifyArtistDto> fetchNotFollowedArtists(List<SpotifyFetchType> fetchTypes);
}
