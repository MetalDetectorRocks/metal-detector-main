package rocks.metaldetector.discogs.client;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import rocks.metaldetector.discogs.api.DiscogsArtist;
import rocks.metaldetector.discogs.api.DiscogsArtistSearchResultContainer;
import rocks.metaldetector.discogs.config.DiscogsConfig;
import rocks.metaldetector.support.exceptions.ExternalServiceException;

@Slf4j
@Service
@Profile({"default", "preview", "prod"})
@AllArgsConstructor
public class DiscogsArtistSearchRestClientImpl implements DiscogsArtistSearchRestClient {

  static final String ARTIST_NAME_SEARCH_URL_FRAGMENT = "/database/search?type=artist&q={artistQueryString}&page={page}&per_page={size}";
  static final String ARTIST_ID_SEARCH_URL_FRAGMENT = "/artists/{artistId}";

  private final RestTemplate discogsRestTemplate;
  private final DiscogsConfig discogsConfig;

  @Override
  public DiscogsArtistSearchResultContainer searchByName(String artistQueryString, int pageNumber, int pageSize) {
    ResponseEntity<DiscogsArtistSearchResultContainer> responseEntity = discogsRestTemplate.getForEntity(
            discogsConfig.getRestBaseUrl() + ARTIST_NAME_SEARCH_URL_FRAGMENT,
            DiscogsArtistSearchResultContainer.class,
            artistQueryString,
            pageNumber,
            pageSize
    );

    DiscogsArtistSearchResultContainer resultContainer = responseEntity.getBody();
    var shouldNotHappen = resultContainer == null || !responseEntity.getStatusCode().is2xxSuccessful();
    if (shouldNotHappen) {
      throw new ExternalServiceException("Could not get search results for query '" + artistQueryString + "' (Response code: " + responseEntity.getStatusCode() + ")");
    }

    return resultContainer;
  }

  @Override
  public DiscogsArtist searchById(String externalId) {
    if (externalId == null || externalId.isEmpty()) {
      throw new IllegalArgumentException("externalId must not be empty");
    }

    ResponseEntity<DiscogsArtist> responseEntity = discogsRestTemplate.getForEntity(
            discogsConfig.getRestBaseUrl() + ARTIST_ID_SEARCH_URL_FRAGMENT,
            DiscogsArtist.class,
            externalId
    );

    DiscogsArtist discogsArtist = responseEntity.getBody();
    var shouldNotHappen = discogsArtist == null || !responseEntity.getStatusCode().is2xxSuccessful();
    if (shouldNotHappen) {
      throw new ExternalServiceException("Could not get artist for externalId '" + externalId + "' (Response code: " + responseEntity.getStatusCode() + ")");
    }

    return discogsArtist;
  }
}
