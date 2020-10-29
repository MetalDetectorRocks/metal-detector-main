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
import rocks.metaldetector.spotify.api.imports.SpotfiyAlbumImportResult;
import rocks.metaldetector.spotify.config.SpotifyProperties;
import rocks.metaldetector.support.exceptions.ExternalServiceException;

import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Map;

import static org.springframework.http.HttpMethod.GET;

@Slf4j
@Service
@Profile({"default", "preview", "prod"})
@AllArgsConstructor
public class SpotifyUserLibraryClientImpl implements SpotifyUserLibraryClient {

  static final int LIMIT = 50;
  static final String OFFSET_PARAMETER_NAME = "offset";
  static final String LIMIT_PARAMETER_NAME = "limit";
  static final String GET_MY_ALBUMS_ENDPOINT = "/v1/me/albums?limit={" + LIMIT_PARAMETER_NAME + "}&" +
                                               "offset={" + OFFSET_PARAMETER_NAME + "}";

  private final RestTemplate spotifyRestTemplate;
  private final SpotifyProperties spotifyProperties;

  @Override
  public SpotfiyAlbumImportResult fetchLikedAlbums(String token, int offset) {
    if (token == null || token.isEmpty()) {
      throw new IllegalArgumentException("token must not be empty");
    }

    HttpEntity<Object> httpEntity = createHttpEntity(token);
    ResponseEntity<SpotfiyAlbumImportResult> responseEntity = spotifyRestTemplate.exchange(
        spotifyProperties.getRestBaseUrl() + GET_MY_ALBUMS_ENDPOINT,
        GET,
        httpEntity,
        SpotfiyAlbumImportResult.class,
        Map.of(OFFSET_PARAMETER_NAME, offset,
               LIMIT_PARAMETER_NAME, LIMIT));

    SpotfiyAlbumImportResult result = responseEntity.getBody();
    var shouldNotHappen = result == null || !responseEntity.getStatusCode().is2xxSuccessful();
    if (shouldNotHappen) {
      throw new ExternalServiceException("Could not get albums from Spotify (Response code: " + responseEntity.getStatusCode() + ")");
    }
    return result;
  }

  private HttpEntity<Object> createHttpEntity(String token) {
    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.setAcceptCharset(Collections.singletonList(Charset.defaultCharset()));
    headers.setBearerAuth(token);
    return new HttpEntity<>(headers);
  }
}
