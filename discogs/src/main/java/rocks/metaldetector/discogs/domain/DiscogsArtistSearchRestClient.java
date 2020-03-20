package rocks.metaldetector.discogs.domain;

import rocks.metaldetector.discogs.fascade.dto.DiscogsArtistDto;
import rocks.metaldetector.discogs.fascade.dto.DiscogsArtistSearchResultDto;
import rocks.metaldetector.discogs.fascade.dto.DiscogsSearchResultDto;

import java.util.Optional;

public interface DiscogsArtistSearchRestClient {

  Optional<DiscogsSearchResultDto<DiscogsArtistSearchResultDto>> searchByName(String artistQueryString, int pageNumber, int pageSize);

  Optional<DiscogsArtistDto> searchById(long artistId);

}
