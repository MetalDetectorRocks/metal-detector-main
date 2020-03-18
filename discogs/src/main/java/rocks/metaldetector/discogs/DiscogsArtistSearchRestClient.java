package rocks.metaldetector.discogs;

import org.springframework.data.domain.Pageable;
import rocks.metaldetector.discogs.api.DiscogsArtist;
import rocks.metaldetector.discogs.api.DiscogsArtistSearchResultContainer;

import java.util.Optional;

public interface DiscogsArtistSearchRestClient {

  Optional<DiscogsArtistSearchResultContainer> searchByName(String artistQueryString, Pageable pageable);

  Optional<DiscogsArtist> searchById(long artistId);

}
