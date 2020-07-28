package rocks.metaldetector.spotify.client;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import rocks.metaldetector.spotify.api.search.SpotifyArtistSearchResultContainer;
import rocks.metaldetector.spotify.config.SpotifyConfig;
import rocks.metaldetector.support.exceptions.ExternalServiceException;

import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Map;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpMethod.GET;

@Slf4j
@Service
@Profile({"default", "preview", "prod"})
@AllArgsConstructor
public class SpotifyArtistSearchClientImpl implements SpotifyArtistSearchClient {

  static final String QUERY_PARAMETER_NAME = "artistQueryString";
  static final String OFFSET_PARAMETER_NAME = "offset";
  static final String LIMIT_PARAMETER_NAME = "limit";
  static final String AUTHORIZATION_HEADER_PREFIX = "Bearer ";
  static final String SEARCH_ENDPOINT = "/v1/search?q={" + QUERY_PARAMETER_NAME + "}&"
                                        + "type=artist&offset={" + OFFSET_PARAMETER_NAME + "}&"
                                        + "limit={" + LIMIT_PARAMETER_NAME + "}";

  private final RestTemplate spotifyRestTemplate;
  private final SpotifyConfig spotifyConfig;

  @Override
  public SpotifyArtistSearchResultContainer searchByName(String authenticationToken, String artistQueryString, int pageNumber, int pageSize) {
    if (artistQueryString == null || artistQueryString.isEmpty()) {
      return SpotifyArtistSearchResultContainer.builder().build();
    }

    HttpEntity<Object> httpEntity = createQueryHttpEntity(authenticationToken);
    String urlEncodedQuery = URLEncoder.encode(artistQueryString, Charset.defaultCharset());
    int offset = pageSize * (pageNumber - 1);

    ResponseEntity<SpotifyArtistSearchResultContainer> responseEntity = spotifyRestTemplate.exchange(
        spotifyConfig.getRestBaseUrl() + SEARCH_ENDPOINT,
        GET,
        httpEntity,
        SpotifyArtistSearchResultContainer.class,
        Map.of(QUERY_PARAMETER_NAME, urlEncodedQuery,
               OFFSET_PARAMETER_NAME, offset,
               LIMIT_PARAMETER_NAME, pageSize)
    );

    SpotifyArtistSearchResultContainer resultContainer = responseEntity.getBody();
    var shouldNotHappen = resultContainer == null || !responseEntity.getStatusCode().is2xxSuccessful();
    if (shouldNotHappen) {
      throw new ExternalServiceException("Could not get search results for query '" + artistQueryString + "' (Response code: " + responseEntity.getStatusCode() + ")");
    }

    return resultContainer;
  }

  private HttpEntity<Object> createQueryHttpEntity(String authenticationToken) {
    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.setAcceptCharset(Collections.singletonList(Charset.defaultCharset()));
    headers.set(AUTHORIZATION, AUTHORIZATION_HEADER_PREFIX + authenticationToken);
    return new HttpEntity<>(headers);
  }
}
