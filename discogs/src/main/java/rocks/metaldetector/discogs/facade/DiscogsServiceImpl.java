package rocks.metaldetector.discogs.facade;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import rocks.metaldetector.discogs.api.DiscogsArtist;
import rocks.metaldetector.discogs.api.DiscogsArtistSearchResultContainer;
import rocks.metaldetector.discogs.client.DiscogsArtistSearchRestClient;
import rocks.metaldetector.discogs.client.transformer.DiscogsArtistSearchResultContainerTransformer;
import rocks.metaldetector.discogs.client.transformer.DiscogsArtistTransformer;
import rocks.metaldetector.discogs.facade.dto.DiscogsArtistDto;
import rocks.metaldetector.discogs.facade.dto.DiscogsArtistSearchResultDto;

@Service
@AllArgsConstructor
public class DiscogsServiceImpl implements DiscogsService {

  private final DiscogsArtistSearchRestClient searchClient;
  private final DiscogsArtistTransformer artistTransformer;
  private final DiscogsArtistSearchResultContainerTransformer searchResultTransformer;

  @Override
  public DiscogsArtistSearchResultDto searchArtistByName(String artistQueryString, int pageNumber, int pageSize) {
    DiscogsArtistSearchResultContainer result = searchClient.searchByName(artistQueryString, pageNumber, pageSize);
    return searchResultTransformer.transform(result, artistQueryString);
  }

  @Override
  public DiscogsArtistDto searchArtistById(String externalId) {
    DiscogsArtist result = searchClient.searchById(externalId);
    return artistTransformer.transform(result);
  }
}
