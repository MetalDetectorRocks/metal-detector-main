package rocks.metaldetector.discogs.domain;

import rocks.metaldetector.discogs.facade.dto.DiscogsArtistDto;
import rocks.metaldetector.discogs.facade.dto.DiscogsArtistSearchResultDto;
import rocks.metaldetector.discogs.facade.dto.DiscogsSearchResultDto;

import java.util.Optional;

public interface DiscogsArtistSearchRestClient {

  Optional<DiscogsSearchResultDto<DiscogsArtistSearchResultDto>> searchByName(String artistQueryString, int pageNumber, int pageSize);

  Optional<DiscogsArtistDto> searchById(long artistId);

}
