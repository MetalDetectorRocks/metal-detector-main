package rocks.metaldetector.discogs;

import rocks.metaldetector.discogs.api.DiscogsArtist;
import rocks.metaldetector.discogs.api.DiscogsArtistSearchResultContainer;

import java.util.Optional;

public interface DiscogsArtistSearchRestClient {

  Optional<DiscogsArtistSearchResultContainer> searchByName(String artistQueryString, int pageNumber, int pageSize);

  Optional<DiscogsArtist> searchById(long artistId);

}
