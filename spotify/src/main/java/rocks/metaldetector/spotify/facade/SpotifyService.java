package rocks.metaldetector.spotify.facade;

import rocks.metaldetector.spotify.facade.dto.SpotifyArtistSearchResultDto;

public interface SpotifyService {

  SpotifyArtistSearchResultDto searchArtists(String artistQueryString, int pageNumber, int pageSize);

}
