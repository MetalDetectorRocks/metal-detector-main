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
import rocks.metaldetector.spotify.api.SpotifyArtist;
import rocks.metaldetector.spotify.api.search.SpotifyArtistSearchResultContainer;
import rocks.metaldetector.spotify.api.search.SpotifyArtistsContainer;
import rocks.metaldetector.spotify.config.SpotifyProperties;
import rocks.metaldetector.support.exceptions.ExternalServiceException;

import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpMethod.GET;

@Slf4j
@Service
@Profile({"default", "preview", "prod"})
@AllArgsConstructor
public class SpotifyArtistSearchClientImpl implements SpotifyArtistSearchClient {

  static final String ID_PARAMETER_NAME = "id";
  static final String QUERY_PARAMETER_NAME = "artistQueryString";
  static final String OFFSET_PARAMETER_NAME = "offset";
  static final String LIMIT_PARAMETER_NAME = "limit";

  static final String GET_ARTIST_ENDPOINT = "/v1/artists/{" + ID_PARAMETER_NAME + "}";
  static final String GET_ARTISTS_ENDPOINT = "/v1/artists?ids={" + ID_PARAMETER_NAME + "}";
  static final String SEARCH_ENDPOINT = "/v1/search?q={" + QUERY_PARAMETER_NAME + "}&"
                                        + "type=artist&offset={" + OFFSET_PARAMETER_NAME + "}&"
                                        + "limit={" + LIMIT_PARAMETER_NAME + "}";

  private final RestTemplate spotifyRestTemplate;
  private final SpotifyProperties spotifyProperties;

  @Override
  public SpotifyArtistSearchResultContainer searchByName(String authorizationToken, String artistQueryString, int pageNumber, int pageSize) {
    if (artistQueryString == null || artistQueryString.isEmpty()) {
      return SpotifyArtistSearchResultContainer.builder().build();
    }

    HttpEntity<Object> httpEntity = createQueryHttpEntity(authorizationToken);
    String urlEncodedQuery = URLEncoder.encode(artistQueryString, Charset.defaultCharset());
    int offset = pageSize * (pageNumber - 1);

    ResponseEntity<SpotifyArtistSearchResultContainer> responseEntity = spotifyRestTemplate.exchange(
        spotifyProperties.getRestBaseUrl() + SEARCH_ENDPOINT,
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

  @Override
  public SpotifyArtist searchById(String authenticationToken, String artistId) {
    if (artistId == null || artistId.isEmpty()) {
      throw new IllegalArgumentException("externalId must not be empty");
    }

    HttpEntity<Object> httpEntity = createQueryHttpEntity(authenticationToken);
    ResponseEntity<SpotifyArtist> responseEntity = spotifyRestTemplate.exchange(
        spotifyProperties.getRestBaseUrl() + GET_ARTIST_ENDPOINT,
        GET,
        httpEntity,
        SpotifyArtist.class,
        Map.of(ID_PARAMETER_NAME, artistId)
    );

    SpotifyArtist spotifyArtist = responseEntity.getBody();
    var shouldNotHappen = spotifyArtist == null || !responseEntity.getStatusCode().is2xxSuccessful();
    if (shouldNotHappen) {
      throw new ExternalServiceException("Could not get artist with id '" + artistId + "' from Spotify (Response code: " + responseEntity.getStatusCode() + ")");
    }

    return spotifyArtist;
  }

  @Override
  public SpotifyArtistsContainer searchByIds(String authenticationToken, List<String> artistIds) {
    if (artistIds == null || artistIds.isEmpty()) {
      throw new IllegalArgumentException("artistIds must not be empty");
    }

    HttpEntity<Object> httpEntity = createQueryHttpEntity(authenticationToken);
    String artistIdsString = String.join(",", artistIds);
    ResponseEntity<SpotifyArtistsContainer> responseEntity = spotifyRestTemplate.exchange(
        spotifyProperties.getRestBaseUrl() + GET_ARTISTS_ENDPOINT,
        GET,
        httpEntity,
        SpotifyArtistsContainer.class,
        Map.of(ID_PARAMETER_NAME, artistIdsString)
    );

    SpotifyArtistsContainer spotifyArtistsContainer = responseEntity.getBody();
    var shouldNotHappen = spotifyArtistsContainer == null || !responseEntity.getStatusCode().is2xxSuccessful();
    if (shouldNotHappen) {
      throw new ExternalServiceException("Could not get artists from Spotify (Response code: " + responseEntity.getStatusCode() + ")");
    }

    return spotifyArtistsContainer;
  }

  private HttpEntity<Object> createQueryHttpEntity(String authenticationToken) {
    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.setAcceptCharset(Collections.singletonList(Charset.defaultCharset()));
    headers.setBearerAuth(authenticationToken);
    return new HttpEntity<>(headers);
  }
}
