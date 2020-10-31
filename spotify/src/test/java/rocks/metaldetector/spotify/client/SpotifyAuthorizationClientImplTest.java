package rocks.metaldetector.spotify.client;

import org.apache.tomcat.util.codec.binary.Base64;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import rocks.metaldetector.spotify.api.authorization.SpotifyAppAuthorizationResponse;
import rocks.metaldetector.spotify.api.authorization.SpotifyUserAuthorizationResponse;
import rocks.metaldetector.spotify.client.SpotifyDtoFactory.SpotfiyUserAuthorizationResponseFactory;
import rocks.metaldetector.spotify.config.SpotifyProperties;
import rocks.metaldetector.support.exceptions.ExternalServiceException;

import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static rocks.metaldetector.spotify.client.SpotifyAuthorizationClientImpl.APP_AUTH_REQUEST_VALUE;
import static rocks.metaldetector.spotify.client.SpotifyAuthorizationClientImpl.AUTHORIZATION_ENDPOINT;
import static rocks.metaldetector.spotify.client.SpotifyAuthorizationClientImpl.CODE_REQUEST_KEY;
import static rocks.metaldetector.spotify.client.SpotifyAuthorizationClientImpl.GRANT_TYPE_KEY;
import static rocks.metaldetector.spotify.client.SpotifyAuthorizationClientImpl.REDIRECT_URI_KEY;
import static rocks.metaldetector.spotify.client.SpotifyAuthorizationClientImpl.REFRESH_TOKEN_KEY;
import static rocks.metaldetector.spotify.client.SpotifyAuthorizationClientImpl.USER_AUTH_REQUEST_VALUE;
import static rocks.metaldetector.spotify.client.SpotifyAuthorizationClientImpl.USER_REFRESH_AUTH_REQUEST_VALUE;
import static rocks.metaldetector.spotify.client.SpotifyDtoFactory.SpotifyAppAuthenticationResponseFactory;

@ExtendWith(MockitoExtension.class)
class SpotifyAuthorizationClientImplTest implements WithAssertions {

  private static final String AUTHORIZATION_HEADER_PREFIX = "Basic ";

  @Mock
  private RestTemplate restTemplate;

  @Mock
  private SpotifyProperties spotifyProperties;

  @InjectMocks
  private SpotifyAuthorizationClientImpl underTest;

  @Captor
  private ArgumentCaptor<HttpEntity<MultiValueMap<String, String>>> argumentCaptor;

  @AfterEach
  void tearDown() {
    reset(restTemplate, spotifyProperties);
  }

  @Nested
  @TestInstance(TestInstance.Lifecycle.PER_CLASS)
  @DisplayName("Tests for app authorization")
  class AppAuthorizationTest {

    @Test
    @DisplayName("a POST call is made on spotify authentication endpoint")
    void test_correct_url_is_called() {
      // given
      var baseUrl = "baseUrl";
      doReturn(baseUrl).when(spotifyProperties).getAuthenticationBaseUrl();
      var expectedUrl = baseUrl + AUTHORIZATION_ENDPOINT;
      SpotifyAppAuthorizationResponse responseMock = SpotifyAppAuthenticationResponseFactory.createDefault();
      doReturn(ResponseEntity.ok(responseMock)).when(restTemplate).postForEntity(anyString(), any(), any());

      // when
      underTest.getAppAuthorizationToken();

      // then
      verify(restTemplate).postForEntity(eq(expectedUrl), any(), any());
    }

    @Test
    @DisplayName("correct request body is set")
    void test_correct_request_body() {
      // given
      SpotifyAppAuthorizationResponse responseMock = SpotifyAppAuthenticationResponseFactory.createDefault();
      doReturn(ResponseEntity.ok(responseMock)).when(restTemplate).postForEntity(anyString(), any(), any());

      // when
      underTest.getAppAuthorizationToken();

      // then
      verify(restTemplate).postForEntity(anyString(), argumentCaptor.capture(), any());
      HttpEntity<MultiValueMap<String, String>> httpEntity = argumentCaptor.getValue();
      assertThat(httpEntity.getBody()).isNotNull();
      assertThat(httpEntity.getBody().size()).isEqualTo(1);
      assertThat(httpEntity.getBody().get(GRANT_TYPE_KEY)).isEqualTo(List.of(APP_AUTH_REQUEST_VALUE));
    }

    @Test
    @DisplayName("correct standard headers are set")
    void test_correct_standard_headers() {
      // given
      SpotifyAppAuthorizationResponse responseMock = SpotifyAppAuthenticationResponseFactory.createDefault();
      doReturn(ResponseEntity.ok(responseMock)).when(restTemplate).postForEntity(anyString(), any(), any());

      // when
      underTest.getAppAuthorizationToken();

      // then
      verify(restTemplate).postForEntity(anyString(), argumentCaptor.capture(), any());
      HttpEntity<MultiValueMap<String, String>> httpEntity = argumentCaptor.getValue();
      HttpHeaders headers = httpEntity.getHeaders();
      assertThat(headers.getContentType()).isEqualTo(MediaType.APPLICATION_FORM_URLENCODED);
      assertThat(headers.getAccept()).isEqualTo(Collections.singletonList(MediaType.APPLICATION_JSON));
      assertThat(headers.getAcceptCharset()).isEqualTo(Collections.singletonList(Charset.defaultCharset()));
    }

    @Test
    @DisplayName("correct authorization header is set")
    void test_correct_authorization_header() {
      // given
      var clientId = "clientId";
      doReturn(clientId).when(spotifyProperties).getClientId();
      var clientSecret = "clientSecret";
      doReturn(clientSecret).when(spotifyProperties).getClientSecret();
      var expectedAuthorizationHeader = AUTHORIZATION_HEADER_PREFIX + Base64.encodeBase64String((clientId + ":" + clientSecret).getBytes());
      SpotifyAppAuthorizationResponse responseMock = SpotifyAppAuthenticationResponseFactory.createDefault();
      doReturn(ResponseEntity.ok(responseMock)).when(restTemplate).postForEntity(anyString(), any(), any());

      // when
      underTest.getAppAuthorizationToken();

      // then
      verify(restTemplate).postForEntity(anyString(), argumentCaptor.capture(), any());
      HttpEntity<MultiValueMap<String, String>> httpEntity = argumentCaptor.getValue();
      HttpHeaders headers = httpEntity.getHeaders();
      assertThat(headers.get(AUTHORIZATION)).isEqualTo(List.of(expectedAuthorizationHeader));
    }

    @Test
    @DisplayName("correct response type is requested")
    void test_correct_response_type() {
      // given
      SpotifyAppAuthorizationResponse responseMock = SpotifyAppAuthenticationResponseFactory.createDefault();
      doReturn(ResponseEntity.ok(responseMock)).when(restTemplate).postForEntity(anyString(), any(), any());

      // when
      underTest.getAppAuthorizationToken();

      // then
      verify(restTemplate).postForEntity(anyString(), any(), eq(SpotifyAppAuthorizationResponse.class));
    }

    @Test
    @DisplayName("accessToken is returned")
    void test_return_value() {
      // given
      SpotifyAppAuthorizationResponse responseMock = SpotifyAppAuthenticationResponseFactory.createDefault();
      doReturn(ResponseEntity.ok(responseMock)).when(restTemplate).postForEntity(anyString(), any(), any());

      // when
      String result = underTest.getAppAuthorizationToken();

      // then
      assertThat(result).isEqualTo(responseMock.getAccessToken());
    }

    @Test
    @DisplayName("if the response is null, an ExternalServiceException is thrown")
    void test_exception_if_response_is_null() {
      // given
      doReturn(ResponseEntity.ok(null)).when(restTemplate).postForEntity(anyString(), any(), any());

      // when
      Throwable throwable = catchThrowable(() -> underTest.getAppAuthorizationToken());

      // then
      assertThat(throwable).isInstanceOf(ExternalServiceException.class);
    }

    @ParameterizedTest(name = "if the status is {0}, an ExternalServiceException is thrown")
    @MethodSource("httpStatusCodeProvider")
    @DisplayName("if the status code is not OK, an ExternalServiceException is thrown")
    void test_exception_if_status_is_not_ok(HttpStatus httpStatus) {
      // given
      SpotifyAppAuthorizationResponse responseMock = SpotifyAppAuthenticationResponseFactory.createDefault();
      doReturn(ResponseEntity.status(httpStatus).body(responseMock)).when(restTemplate).postForEntity(anyString(), any(), any());

      // when
      Throwable throwable = catchThrowable(() -> underTest.getAppAuthorizationToken());

      // then
      assertThat(throwable).isInstanceOf(ExternalServiceException.class);
    }

    private Stream<Arguments> httpStatusCodeProvider() {
      return Stream.of(HttpStatus.values()).filter(status -> !status.is2xxSuccessful()).map(Arguments::of);
    }
  }

  @Nested
  @TestInstance(TestInstance.Lifecycle.PER_CLASS)
  @DisplayName("Tests for user authentication")
  class UserAuthorizationTest {

    @Test
    @DisplayName("a POST call is made on spotify authorization endpoint")
    void test_correct_url_called() {
      // given
      var baseUrl = "baseUrl";
      doReturn(baseUrl).when(spotifyProperties).getAuthenticationBaseUrl();
      SpotifyUserAuthorizationResponse responseMock = SpotfiyUserAuthorizationResponseFactory.createDefault();
      doReturn(ResponseEntity.ok(responseMock)).when(restTemplate).postForEntity(anyString(), any(), any());

      // when
      underTest.getUserAuthorizationToken("code");

      // then
      verify(restTemplate).postForEntity(eq(baseUrl + AUTHORIZATION_ENDPOINT), any(), any());
    }

    @Test
    @DisplayName("correct request body is set")
    void test_correct_request_body() {
      // given
      var code = "code";
      var host = "host";
      var expectedRedirectUri = host + "/profile/spotify-callback";;
      SpotifyUserAuthorizationResponse responseMock = SpotfiyUserAuthorizationResponseFactory.createDefault();
      doReturn(ResponseEntity.ok(responseMock)).when(restTemplate).postForEntity(anyString(), any(), any());
      doReturn(host).when(spotifyProperties).getApplicationHostUrl();

      // when
      underTest.getUserAuthorizationToken(code);

      // then
      verify(restTemplate).postForEntity(anyString(), argumentCaptor.capture(), any());
      HttpEntity<MultiValueMap<String, String>> httpEntity = argumentCaptor.getValue();
      assertThat(httpEntity.getBody()).isNotNull();
      assertThat(httpEntity.getBody().size()).isEqualTo(3);
      assertThat(httpEntity.getBody().get(GRANT_TYPE_KEY)).isEqualTo(List.of(USER_AUTH_REQUEST_VALUE));
      assertThat(httpEntity.getBody().get(CODE_REQUEST_KEY)).isEqualTo(List.of(code));
      assertThat(httpEntity.getBody().get(REDIRECT_URI_KEY)).isEqualTo(List.of(expectedRedirectUri));
    }

    @Test
    @DisplayName("correct standard headers are set")
    void test_correct_standard_headers() {
      // given
      SpotifyUserAuthorizationResponse responseMock = SpotfiyUserAuthorizationResponseFactory.createDefault();
      doReturn(ResponseEntity.ok(responseMock)).when(restTemplate).postForEntity(anyString(), any(), any());

      // when
      underTest.getUserAuthorizationToken("code");

      // then
      verify(restTemplate).postForEntity(anyString(), argumentCaptor.capture(), any());
      HttpEntity<MultiValueMap<String, String>> httpEntity = argumentCaptor.getValue();
      HttpHeaders headers = httpEntity.getHeaders();
      assertThat(headers.getContentType()).isEqualTo(MediaType.APPLICATION_FORM_URLENCODED);
      assertThat(headers.getAccept()).isEqualTo(Collections.singletonList(MediaType.APPLICATION_JSON));
      assertThat(headers.getAcceptCharset()).isEqualTo(Collections.singletonList(Charset.defaultCharset()));
    }

    @Test
    @DisplayName("correct authorization header is set")
    void test_correct_authorization_header() {
      // given
      var clientId = "clientId";
      doReturn(clientId).when(spotifyProperties).getClientId();
      var clientSecret = "clientSecret";
      doReturn(clientSecret).when(spotifyProperties).getClientSecret();
      var expectedAuthorizationHeader = AUTHORIZATION_HEADER_PREFIX + Base64.encodeBase64String((clientId + ":" + clientSecret).getBytes());
      SpotifyUserAuthorizationResponse responseMock = SpotfiyUserAuthorizationResponseFactory.createDefault();
      doReturn(ResponseEntity.ok(responseMock)).when(restTemplate).postForEntity(anyString(), any(), any());

      // when
      underTest.getUserAuthorizationToken("code");

      // then
      verify(restTemplate).postForEntity(anyString(), argumentCaptor.capture(), any());
      HttpEntity<MultiValueMap<String, String>> httpEntity = argumentCaptor.getValue();
      HttpHeaders headers = httpEntity.getHeaders();
      assertThat(headers.get(AUTHORIZATION)).isEqualTo(List.of(expectedAuthorizationHeader));
    }

    @Test
    @DisplayName("correct response type is requested")
    void test_correct_response_type() {
      // given
      SpotifyUserAuthorizationResponse responseMock = SpotfiyUserAuthorizationResponseFactory.createDefault();
      doReturn(ResponseEntity.ok(responseMock)).when(restTemplate).postForEntity(anyString(), any(), any());

      // when
      underTest.getUserAuthorizationToken("code");

      // then
      verify(restTemplate).postForEntity(anyString(), any(), eq(SpotifyUserAuthorizationResponse.class));
    }

    @Test
    @DisplayName("SpotifyUserAuthorizationResponse is returned")
    void test_return_value() {
      // given
      SpotifyUserAuthorizationResponse responseMock = SpotfiyUserAuthorizationResponseFactory.createDefault();
      doReturn(ResponseEntity.ok(responseMock)).when(restTemplate).postForEntity(anyString(), any(), any());

      // when
      var result = underTest.getUserAuthorizationToken("code");

      // then
      assertThat(result).isEqualTo(responseMock);
    }

    @Test
    @DisplayName("if the response is null, an ExternalServiceException is thrown")
    void test_exception_if_response_is_null() {
      // given
      doReturn(ResponseEntity.ok(null)).when(restTemplate).postForEntity(anyString(), any(), any());

      // when
      Throwable throwable = catchThrowable(() -> underTest.getUserAuthorizationToken("code"));

      // then
      assertThat(throwable).isInstanceOf(ExternalServiceException.class);
    }

    @ParameterizedTest(name = "if the status is {0}, an ExternalServiceException is thrown")
    @MethodSource("httpStatusCodeProvider")
    @DisplayName("if the status code is not OK, an ExternalServiceException is thrown")
    void test_exception_if_status_is_not_ok(HttpStatus httpStatus) {
      // given
      SpotifyUserAuthorizationResponse responseMock = SpotfiyUserAuthorizationResponseFactory.createDefault();
      doReturn(ResponseEntity.status(httpStatus).body(responseMock)).when(restTemplate).postForEntity(anyString(), any(), any());

      // when
      Throwable throwable = catchThrowable(() -> underTest.getUserAuthorizationToken("code"));

      // then
      assertThat(throwable).isInstanceOf(ExternalServiceException.class);
    }

    private Stream<Arguments> httpStatusCodeProvider() {
      return Stream.of(HttpStatus.values()).filter(status -> !status.is2xxSuccessful()).map(Arguments::of);
    }
  }

  @Nested
  @TestInstance(TestInstance.Lifecycle.PER_CLASS)
  @DisplayName("Tests for refreshing user authentication")
  class RefreshUserAuthorizationTest {

    @Test
    @DisplayName("a POST call is made on spotify authorization endpoint")
    void test_correct_url_called() {
      // given
      var baseUrl = "baseUrl";
      doReturn(baseUrl).when(spotifyProperties).getAuthenticationBaseUrl();
      SpotifyUserAuthorizationResponse responseMock = SpotfiyUserAuthorizationResponseFactory.createDefault();
      doReturn(ResponseEntity.ok(responseMock)).when(restTemplate).postForEntity(anyString(), any(), any());

      // when
      underTest.refreshUserAuthorizationToken("refreshToken");

      // then
      verify(restTemplate).postForEntity(eq(baseUrl + AUTHORIZATION_ENDPOINT), any(), any());
    }

    @Test
    @DisplayName("correct request body is set")
    void test_correct_request_body() {
      // given
      var refreshToken = "refreshToken";
      SpotifyUserAuthorizationResponse responseMock = SpotfiyUserAuthorizationResponseFactory.createDefault();
      doReturn(ResponseEntity.ok(responseMock)).when(restTemplate).postForEntity(anyString(), any(), any());

      // when
      underTest.refreshUserAuthorizationToken(refreshToken);

      // then
      verify(restTemplate).postForEntity(anyString(), argumentCaptor.capture(), any());
      HttpEntity<MultiValueMap<String, String>> httpEntity = argumentCaptor.getValue();
      assertThat(httpEntity.getBody()).isNotNull();
      assertThat(httpEntity.getBody().size()).isEqualTo(2);
      assertThat(httpEntity.getBody().get(GRANT_TYPE_KEY)).isEqualTo(List.of(USER_REFRESH_AUTH_REQUEST_VALUE));
      assertThat(httpEntity.getBody().get(REFRESH_TOKEN_KEY)).isEqualTo(List.of(refreshToken));
    }

    @Test
    @DisplayName("correct standard headers are set")
    void test_correct_standard_headers() {
      // given
      SpotifyUserAuthorizationResponse responseMock = SpotfiyUserAuthorizationResponseFactory.createDefault();
      doReturn(ResponseEntity.ok(responseMock)).when(restTemplate).postForEntity(anyString(), any(), any());

      // when
      underTest.refreshUserAuthorizationToken("refreshToken");

      // then
      verify(restTemplate).postForEntity(anyString(), argumentCaptor.capture(), any());
      HttpEntity<MultiValueMap<String, String>> httpEntity = argumentCaptor.getValue();
      HttpHeaders headers = httpEntity.getHeaders();
      assertThat(headers.getContentType()).isEqualTo(MediaType.APPLICATION_FORM_URLENCODED);
      assertThat(headers.getAccept()).isEqualTo(Collections.singletonList(MediaType.APPLICATION_JSON));
      assertThat(headers.getAcceptCharset()).isEqualTo(Collections.singletonList(Charset.defaultCharset()));
    }

    @Test
    @DisplayName("correct authorization header is set")
    void test_correct_authorization_header() {
      // given
      var clientId = "clientId";
      doReturn(clientId).when(spotifyProperties).getClientId();
      var clientSecret = "clientSecret";
      doReturn(clientSecret).when(spotifyProperties).getClientSecret();
      var expectedAuthorizationHeader = AUTHORIZATION_HEADER_PREFIX + Base64.encodeBase64String((clientId + ":" + clientSecret).getBytes());
      SpotifyUserAuthorizationResponse responseMock = SpotfiyUserAuthorizationResponseFactory.createDefault();
      doReturn(ResponseEntity.ok(responseMock)).when(restTemplate).postForEntity(anyString(), any(), any());

      // when
      underTest.refreshUserAuthorizationToken("refreshToken");

      // then
      verify(restTemplate).postForEntity(anyString(), argumentCaptor.capture(), any());
      HttpEntity<MultiValueMap<String, String>> httpEntity = argumentCaptor.getValue();
      HttpHeaders headers = httpEntity.getHeaders();
      assertThat(headers.get(AUTHORIZATION)).isEqualTo(List.of(expectedAuthorizationHeader));
    }

    @Test
    @DisplayName("correct response type is requested")
    void test_correct_response_type() {
      // given
      SpotifyUserAuthorizationResponse responseMock = SpotfiyUserAuthorizationResponseFactory.createDefault();
      doReturn(ResponseEntity.ok(responseMock)).when(restTemplate).postForEntity(anyString(), any(), any());

      // when
      underTest.refreshUserAuthorizationToken("refreshToken");

      // then
      verify(restTemplate).postForEntity(anyString(), any(), eq(SpotifyUserAuthorizationResponse.class));
    }

    @Test
    @DisplayName("SpotifyUserAuthorizationResponse is returned")
    void test_return_value() {
      // given
      SpotifyUserAuthorizationResponse responseMock = SpotfiyUserAuthorizationResponseFactory.createDefault();
      doReturn(ResponseEntity.ok(responseMock)).when(restTemplate).postForEntity(anyString(), any(), any());

      // when
      var result = underTest.refreshUserAuthorizationToken("refreshToken");

      // then
      assertThat(result).isEqualTo(responseMock);
    }

    @Test
    @DisplayName("if the response is null, an ExternalServiceException is thrown")
    void test_exception_if_response_is_null() {
      // given
      doReturn(ResponseEntity.ok(null)).when(restTemplate).postForEntity(anyString(), any(), any());

      // when
      Throwable throwable = catchThrowable(() -> underTest.refreshUserAuthorizationToken("refreshToken"));

      // then
      assertThat(throwable).isInstanceOf(ExternalServiceException.class);
    }

    @ParameterizedTest(name = "if the status is {0}, an ExternalServiceException is thrown")
    @MethodSource("httpStatusCodeProvider")
    @DisplayName("if the status code is not OK, an ExternalServiceException is thrown")
    void test_exception_if_status_is_not_ok(HttpStatus httpStatus) {
      // given
      SpotifyUserAuthorizationResponse responseMock = SpotfiyUserAuthorizationResponseFactory.createDefault();
      doReturn(ResponseEntity.status(httpStatus).body(responseMock)).when(restTemplate).postForEntity(anyString(), any(), any());

      // when
      Throwable throwable = catchThrowable(() -> underTest.refreshUserAuthorizationToken("refreshToken"));

      // then
      assertThat(throwable).isInstanceOf(ExternalServiceException.class);
    }

    private Stream<Arguments> httpStatusCodeProvider() {
      return Stream.of(HttpStatus.values()).filter(status -> !status.is2xxSuccessful()).map(Arguments::of);
    }
  }
}