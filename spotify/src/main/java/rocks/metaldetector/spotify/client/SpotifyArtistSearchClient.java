package rocks.metaldetector.spotify.client;

import rocks.metaldetector.spotify.api.SpotifyArtist;
import rocks.metaldetector.spotify.api.search.SpotifyArtistSearchResultContainer;
import rocks.metaldetector.spotify.api.search.SpotifyArtistsContainer;

import java.util.List;

public interface SpotifyArtistSearchClient {

  SpotifyArtistSearchResultContainer searchByName(String authorizationToken, String artistQueryString, int pageNumber, int pageSize);

  SpotifyArtist searchById(String authenticationToken, String artistId);

  SpotifyArtistsContainer searchByIds(String authenticationToken, List<String> artistIds);

}
