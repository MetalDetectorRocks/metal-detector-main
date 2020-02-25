package rocks.metaldetector.service.discogs;

import org.springframework.data.domain.Pageable;
import rocks.metaldetector.web.dto.discogs.artist.DiscogsArtist;
import rocks.metaldetector.web.dto.discogs.search.DiscogsArtistSearchResultContainer;

import java.util.Optional;

public interface DiscogsArtistSearchRestClient {

  Optional<DiscogsArtistSearchResultContainer> searchByName(String artistQueryString, Pageable pageable);

  Optional<DiscogsArtist> searchById(long artistId);

}
