package rocks.metaldetector.spotify.client;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import rocks.metaldetector.spotify.api.authorization.SpotifyAppAuthorizationResponse;
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
  static final String APP_AUTH_REQUEST_VALUE = "client_credentials";
  static final String USER_AUTH_REQUEST_VALUE = "authorization_code";

  private final RestTemplate spotifyRestTemplate;
  private final SpotifyProperties spotifyProperties;

  @Override
  @Cacheable("spotifyAppAuthorizationToken")
  public String getAppAuthorizationToken() {
    MultiValueMap<String, String> request = new LinkedMultiValueMap<>();
    request.add(GRANT_TYPE_KEY, APP_AUTH_REQUEST_VALUE);

    HttpEntity<MultiValueMap<String, String>> httpEntity = createHttpEntity(request);
    ResponseEntity<SpotifyAppAuthorizationResponse> responseEntity = spotifyRestTemplate.postForEntity(
        spotifyProperties.getAuthenticationBaseUrl() + AUTHORIZATION_ENDPOINT, httpEntity, SpotifyAppAuthorizationResponse.class);

    SpotifyAppAuthorizationResponse resultContainer = responseEntity.getBody();
    var shouldNotHappen = resultContainer == null || !responseEntity.getStatusCode().is2xxSuccessful();
    if (shouldNotHappen) {
      throw new ExternalServiceException("Could not get app authorization token from Spotify (Response code: " + responseEntity.getStatusCode() + ")");
    }

    return resultContainer.getAccessToken();
  }

  @Override
  public SpotifyUserAuthorizationResponse getUserAuthorizationToken(String code) {
    MultiValueMap<String, String> request = new LinkedMultiValueMap<>();
    request.add(GRANT_TYPE_KEY, USER_AUTH_REQUEST_VALUE);
    request.add(CODE_REQUEST_KEY, code);
    request.add(REDIRECT_URI_KEY, spotifyProperties.getApplicationHostUrl() + Endpoints.Frontend.PROFILE + Endpoints.Frontend.SPOTIFY_CALLBACK);

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
}
