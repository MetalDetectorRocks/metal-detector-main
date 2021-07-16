package rocks.metaldetector.spotify.client;

import rocks.metaldetector.spotify.api.SpotifyArtist;
import rocks.metaldetector.spotify.api.search.SpotifyArtistSearchResultContainer;
import rocks.metaldetector.spotify.api.search.SpotifyArtistsContainer;

import java.util.List;

public interface SpotifyArtistSearchClient {

  SpotifyArtistSearchResultContainer searchByName(String artistQueryString, int pageNumber, int pageSize);

  SpotifyArtist searchById(String artistId);

  SpotifyArtistsContainer searchByIds(List<String> artistIds);

}
