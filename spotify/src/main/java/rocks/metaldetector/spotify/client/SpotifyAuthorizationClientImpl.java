package rocks.metaldetector.spotify.client;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestOperations;
import rocks.metaldetector.spotify.api.authorization.SpotifyUserAuthorizationResponse;
import rocks.metaldetector.spotify.config.SpotifyProperties;
import rocks.metaldetector.support.Endpoints;
import rocks.metaldetector.support.exceptions.ExternalServiceException;

import java.nio.charset.Charset;
import java.util.Collections;

@Slf4j
@Service
@Profile({"default", "preview", "prod"})
@AllArgsConstructor
public class SpotifyAuthorizationClientImpl implements SpotifyAuthorizationClient {

  static final String AUTHORIZATION_ENDPOINT = "/api/token";
  static final String GRANT_TYPE_KEY = "grant_type";
  static final String CODE_REQUEST_KEY = "code";
  static final String REDIRECT_URI_KEY = "redirect_uri";
  static final String REFRESH_TOKEN_KEY = "refresh_token";
  static final String USER_AUTH_REQUEST_VALUE = "authorization_code";
  static final String USER_REFRESH_AUTH_REQUEST_VALUE = "refresh_token";

  private final RestOperations spotifyRestTemplate;
  private final SpotifyProperties spotifyProperties;

  @Override
  public SpotifyUserAuthorizationResponse getUserAuthorizationToken(String code) {
    MultiValueMap<String, String> request = new LinkedMultiValueMap<>();
    request.add(GRANT_TYPE_KEY, USER_AUTH_REQUEST_VALUE);
    request.add(CODE_REQUEST_KEY, code);
    request.add(REDIRECT_URI_KEY, spotifyProperties.getApplicationHostUrl() + Endpoints.Frontend.SPOTIFY_SYNCHRONIZATION + Endpoints.Frontend.SPOTIFY_CALLBACK);

    return callAuthorizationEndpoint(request);
  }

  @Override
  public SpotifyUserAuthorizationResponse refreshUserAuthorizationToken(String refreshToken) {
    MultiValueMap<String, String> request = new LinkedMultiValueMap<>();
    request.add(GRANT_TYPE_KEY, USER_REFRESH_AUTH_REQUEST_VALUE);
    request.add(REFRESH_TOKEN_KEY, refreshToken);

    return callAuthorizationEndpoint(request);
  }

  private HttpEntity<MultiValueMap<String, String>> createHttpEntity(MultiValueMap<String, String> request) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.setAcceptCharset(Collections.singletonList(Charset.defaultCharset()));
    headers.setBasicAuth(createAuthorizationHeader());
    return new HttpEntity<>(request, headers);
  }

  private String createAuthorizationHeader() {
    return Base64.encodeBase64String((spotifyProperties.getClientId() + ":" + spotifyProperties.getClientSecret()).getBytes());
  }

  private SpotifyUserAuthorizationResponse callAuthorizationEndpoint(MultiValueMap<String, String> request) {
    HttpEntity<MultiValueMap<String, String>> httpEntity = createHttpEntity(request);
    ResponseEntity<SpotifyUserAuthorizationResponse> responseEntity = spotifyRestTemplate.postForEntity(
        spotifyProperties.getAuthenticationBaseUrl() + AUTHORIZATION_ENDPOINT, httpEntity, SpotifyUserAuthorizationResponse.class);

    SpotifyUserAuthorizationResponse resultContainer = responseEntity.getBody();
    var shouldNotHappen = resultContainer == null || !responseEntity.getStatusCode().is2xxSuccessful();
    if (shouldNotHappen) {
      throw new ExternalServiceException("Could not get user authorization token from Spotify (Response code: " + responseEntity.getStatusCode() + ")");
    }

    return resultContainer;
  }
}
