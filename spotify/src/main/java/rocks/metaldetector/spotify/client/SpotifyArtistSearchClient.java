package rocks.metaldetector.spotify.client;

import rocks.metaldetector.spotify.api.search.SpotifyArtistSearchResultContainer;

public interface SpotifyArtistSearchClient {

  SpotifyArtistSearchResultContainer searchByName(String authenticationToken, String artistQueryString, int pageNumber, int pageSize);

}
