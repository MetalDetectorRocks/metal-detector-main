package rocks.metaldetector.spotify.client;

import org.apache.tomcat.util.codec.binary.Base64;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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
import rocks.metaldetector.spotify.api.authentication.SpotifyAuthenticationResponse;
import rocks.metaldetector.spotify.config.SpotifyConfig;
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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static rocks.metaldetector.spotify.client.SpotifyAuthenticationClientImpl.AUTHORIZATION_ENDPOINT;
import static rocks.metaldetector.spotify.client.SpotifyAuthenticationClientImpl.AUTHORIZATION_HEADER_PREFIX;
import static rocks.metaldetector.spotify.client.SpotifyAuthenticationClientImpl.REQUEST_KEY;
import static rocks.metaldetector.spotify.client.SpotifyAuthenticationClientImpl.REQUEST_VALUE;
import static rocks.metaldetector.spotify.client.SpotifyDtoFactory.SpotifyAuthenticationResponseFactory;

@ExtendWith(MockitoExtension.class)
class SpotifyAuthenticationClientImplTest implements WithAssertions {

  @Mock
  private RestTemplate restTemplate;

  @Mock
  private SpotifyConfig spotifyConfig;

  @InjectMocks
  private SpotifyAuthenticationClientImpl underTest;

  @Captor
  private ArgumentCaptor<HttpEntity<MultiValueMap<String, String>>> argumentCaptor;

  @AfterEach
  void tearDown() {
    reset(restTemplate, spotifyConfig);
  }

  @Test
  @DisplayName("a POST call is made on spotify authentication endpoint")
  void test_correct_url_is_called() {
    // given
    var baseUrl = "baseUrl";
    doReturn(baseUrl).when(spotifyConfig).getAuthenticationBaseUrl();
    var expectedUrl = baseUrl + AUTHORIZATION_ENDPOINT;
    SpotifyAuthenticationResponse responseMock = SpotifyAuthenticationResponseFactory.createDefault();
    doReturn(ResponseEntity.ok(responseMock)).when(restTemplate).postForEntity(anyString(), any(), any());

    // when
    underTest.getAuthenticationToken();

    // then
    verify(restTemplate, times(1)).postForEntity(eq(expectedUrl), any(), any());
  }

  @Test
  @DisplayName("correct request body is set")
  void test_correct_requerst_body() {
    // given
    SpotifyAuthenticationResponse responseMock = SpotifyAuthenticationResponseFactory.createDefault();
    doReturn(ResponseEntity.ok(responseMock)).when(restTemplate).postForEntity(anyString(), any(), any());

    // when
    underTest.getAuthenticationToken();

    // then
    verify(restTemplate, times(1)).postForEntity(anyString(), argumentCaptor.capture(), any());
    HttpEntity<MultiValueMap<String, String>> httpEntity = argumentCaptor.getValue();
    assertThat(httpEntity.getBody()).isNotNull();
    assertThat(httpEntity.getBody().size()).isEqualTo(1);
    assertThat(httpEntity.getBody().get(REQUEST_KEY)).isEqualTo(List.of(REQUEST_VALUE));
  }

  @Test
  @DisplayName("correct standard headers are set")
  void test_correct_standard_headers() {
    // given
    SpotifyAuthenticationResponse responseMock = SpotifyAuthenticationResponseFactory.createDefault();
    doReturn(ResponseEntity.ok(responseMock)).when(restTemplate).postForEntity(anyString(), any(), any());

    // when
    underTest.getAuthenticationToken();

    // then
    verify(restTemplate, times(1)).postForEntity(anyString(), argumentCaptor.capture(), any());
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
    doReturn(clientId).when(spotifyConfig).getClientId();
    var clientSecret = "clientSecret";
    doReturn(clientSecret).when(spotifyConfig).getClientSecret();
    var expectedAuthorizationHeader = AUTHORIZATION_HEADER_PREFIX + Base64.encodeBase64String((clientId + ":" + clientSecret).getBytes());
    SpotifyAuthenticationResponse responseMock = SpotifyAuthenticationResponseFactory.createDefault();
    doReturn(ResponseEntity.ok(responseMock)).when(restTemplate).postForEntity(anyString(), any(), any());

    // when
    underTest.getAuthenticationToken();

    // then
    verify(restTemplate, times(1)).postForEntity(anyString(), argumentCaptor.capture(), any());
    HttpEntity<MultiValueMap<String, String>> httpEntity = argumentCaptor.getValue();
    HttpHeaders headers = httpEntity.getHeaders();
    assertThat(headers.get(AUTHORIZATION)).isEqualTo(List.of(expectedAuthorizationHeader));
  }

  @Test
  @DisplayName("correct response type is requested")
  void test_correct_response_type() {
    // given
    SpotifyAuthenticationResponse responseMock = SpotifyAuthenticationResponseFactory.createDefault();
    doReturn(ResponseEntity.ok(responseMock)).when(restTemplate).postForEntity(anyString(), any(), any());

    // when
    underTest.getAuthenticationToken();

    // then
    verify(restTemplate, times(1)).postForEntity(anyString(), any(), eq(SpotifyAuthenticationResponse.class));
  }

  @Test
  @DisplayName("accessToken is returned")
  void test_return_value() {
    // given
    SpotifyAuthenticationResponse responseMock = SpotifyAuthenticationResponseFactory.createDefault();
    doReturn(ResponseEntity.ok(responseMock)).when(restTemplate).postForEntity(anyString(), any(), any());

    // when
    String result = underTest.getAuthenticationToken();

    // then
    assertThat(result).isEqualTo(responseMock.getAccessToken());
  }

  @Test
  @DisplayName("if the response is null, an ExternalServiceException is thrown")
  void test_exception_if_response_is_null() {
    // given
    doReturn(ResponseEntity.ok(null)).when(restTemplate).postForEntity(anyString(), any(), any());

    // when
    Throwable throwable = catchThrowable(() -> underTest.getAuthenticationToken());

    // then
    assertThat(throwable).isInstanceOf(ExternalServiceException.class);
  }

  @ParameterizedTest(name = "if the status is {0}, an ExternalServiceException is thrown")
  @MethodSource("httpStatusCodeProvider")
  @DisplayName("if the status code is not OK, an ExternalServiceException is thrown")
  void test_exception_if_status_is_not_ok(HttpStatus httpStatus) {
    // given
    SpotifyAuthenticationResponse responseMock = SpotifyAuthenticationResponseFactory.createDefault();
    doReturn(ResponseEntity.status(httpStatus).body(responseMock)).when(restTemplate).postForEntity(anyString(), any(), any());

    // when
    Throwable throwable = catchThrowable(() -> underTest.getAuthenticationToken());

    // then
    assertThat(throwable).isInstanceOf(ExternalServiceException.class);
  }

  private static Stream<Arguments> httpStatusCodeProvider() {
    return Stream.of(HttpStatus.values()).filter(status -> !status.is2xxSuccessful()).map(Arguments::of);
  }
}