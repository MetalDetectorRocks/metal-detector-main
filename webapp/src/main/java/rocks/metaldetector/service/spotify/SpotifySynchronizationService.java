package rocks.metaldetector.service.spotify;

import rocks.metaldetector.spotify.facade.dto.SpotifyArtistDto;

import java.util.List;

public interface SpotifySynchronizationService {

  List<String> synchronizeArtists(List<String> artistIds);

  List<SpotifyArtistDto> fetchSavedArtists(List<SpotifyFetchType> fetchTypes);
}
