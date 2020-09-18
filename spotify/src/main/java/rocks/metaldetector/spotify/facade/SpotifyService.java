package rocks.metaldetector.spotify.facade;

import rocks.metaldetector.spotify.facade.dto.SpotifyArtistDto;
import rocks.metaldetector.spotify.facade.dto.SpotifyArtistSearchResultDto;

public interface SpotifyService {

  SpotifyArtistSearchResultDto searchArtistByName(String artistQueryString, int pageNumber, int pageSize);

  SpotifyArtistDto searchArtistById(String artistId);

  String getSpotifyAuthorizationUrl();

}
