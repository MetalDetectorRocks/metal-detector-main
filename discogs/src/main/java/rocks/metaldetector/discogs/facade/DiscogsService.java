package rocks.metaldetector.discogs.facade;

import rocks.metaldetector.discogs.facade.dto.DiscogsArtistDto;
import rocks.metaldetector.discogs.facade.dto.DiscogsArtistSearchResultDto;

public interface DiscogsService {

  DiscogsArtistSearchResultDto searchArtistByName(String artistQueryString, int pageNumber, int pageSize);

  DiscogsArtistDto searchArtistById(String externalId);
}
