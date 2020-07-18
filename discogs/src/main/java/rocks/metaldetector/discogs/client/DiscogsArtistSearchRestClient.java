package rocks.metaldetector.discogs.client;

import rocks.metaldetector.discogs.api.DiscogsArtist;
import rocks.metaldetector.discogs.api.DiscogsArtistSearchResultContainer;

public interface DiscogsArtistSearchRestClient {

  DiscogsArtistSearchResultContainer searchByName(String artistQueryString, int pageNumber, int pageSize);

  DiscogsArtist searchById(String externalId);

}
