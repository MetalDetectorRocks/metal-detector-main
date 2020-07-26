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
import org.springframework.web.client.RestTemplate;
import rocks.metaldetector.spotify.api.authentication.SpotifyAuthenticationResponse;
import rocks.metaldetector.spotify.config.SpotifyConfig;
import rocks.metaldetector.support.exceptions.ExternalServiceException;

import java.nio.charset.Charset;
import java.util.Collections;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Slf4j
@Service
@Profile({"default", "preview", "prod"})
@AllArgsConstructor
public class SpotifyAuthenticationClientImpl implements SpotifyAuthenticationClient {

  static final String AUTHORIZATION_ENDPOINT = "/api/token";
  static final String REQUEST_KEY = "grant_type";
  static final String REQUEST_VALUE = "client_credentials";
  static final String AUTHORIZATION_HEADER_PREFIX = "Basic ";

  private final RestTemplate spotifyRestTemplate;
  private final SpotifyConfig spotifyConfig;

  @Override
  public String getAuthenticationToken() {
    HttpEntity<MultiValueMap<String, String>> httpEntity = createHttpEntity();
    ResponseEntity<SpotifyAuthenticationResponse> responseEntity = spotifyRestTemplate.postForEntity(
        spotifyConfig.getAuthenticationBaseUrl() + AUTHORIZATION_ENDPOINT, httpEntity, SpotifyAuthenticationResponse.class);

    SpotifyAuthenticationResponse resultContainer = responseEntity.getBody();
    var shouldNotHappen = resultContainer == null || !responseEntity.getStatusCode().is2xxSuccessful();
    if (shouldNotHappen) {
      throw new ExternalServiceException("Could not get authentication token from Spotify (Response code: " + responseEntity.getStatusCode() + ")");
    }

    return resultContainer.getAccessToken();
  }

  private HttpEntity<MultiValueMap<String, String>> createHttpEntity() {
    MultiValueMap<String, String> request = new LinkedMultiValueMap<>();
    request.add(REQUEST_KEY, REQUEST_VALUE);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.setAcceptCharset(Collections.singletonList(Charset.defaultCharset()));
    headers.set(AUTHORIZATION, createAuthorizationHeader());

    return new HttpEntity<>(request, headers);
  }

  private String createAuthorizationHeader() {
    return AUTHORIZATION_HEADER_PREFIX + Base64.encodeBase64String((spotifyConfig.getClientId() + ":" + spotifyConfig.getClientSecret()).getBytes());
  }
}
