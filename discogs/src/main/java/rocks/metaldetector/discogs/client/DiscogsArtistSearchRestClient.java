package rocks.metaldetector.discogs.client;

import rocks.metaldetector.discogs.facade.dto.DiscogsArtistDto;
import rocks.metaldetector.discogs.facade.dto.DiscogsArtistSearchResultDto;

import java.util.Optional;

public interface DiscogsArtistSearchRestClient {

  Optional<DiscogsArtistSearchResultDto> searchByName(String artistQueryString, int pageNumber, int pageSize);

  Optional<DiscogsArtistDto> searchById(long artistId);

}
