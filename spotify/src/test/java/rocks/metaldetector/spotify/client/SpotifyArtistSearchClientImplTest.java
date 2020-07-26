package rocks.metaldetector.spotify.client;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import rocks.metaldetector.spotify.api.search.SpotifyArtistSearchResultContainer;
import rocks.metaldetector.spotify.config.SpotifyConfig;
import rocks.metaldetector.support.exceptions.ExternalServiceException;

import java.nio.charset.Charset;
import java.util.Collections;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpMethod.GET;
import static rocks.metaldetector.spotify.client.SpotifyArtistSearchClientImpl.AUTHORIZATION_HEADER_PREFIX;
import static rocks.metaldetector.spotify.client.SpotifyArtistSearchClientImpl.SEARCH_ENDPOINT;
import static rocks.metaldetector.spotify.client.SpotifyDtoFactory.SpotifyArtistSearchResultContainerFactory;

@ExtendWith(MockitoExtension.class)
class SpotifyArtistSearchClientImplTest implements WithAssertions {

  @Mock
  private RestTemplate restTemplate;

  @Mock
  private SpotifyConfig spotifyConfig;

  @InjectMocks
  private SpotifyArtistSearchClientImpl underTest;

  @Captor
  private ArgumentCaptor<HttpEntity<Object>> argumentCaptor;

  @AfterEach
  void tearDown() {
    reset(restTemplate, spotifyConfig);
  }

  @ParameterizedTest(name = "empty result is returned for input '{0}'")
  @MethodSource("invalidQueryInputProvider")
  @DisplayName("empty result is returned for invalid query input")
  void test_invalid_input(String invalidQuery) {
    // given
    SpotifyArtistSearchResultContainer emptyResult = SpotifyArtistSearchResultContainer.builder().build();

    // when
    SpotifyArtistSearchResultContainer result = underTest.searchByName("token", invalidQuery, 1, 10);

    // then
    assertThat(result).isEqualTo(emptyResult);
  }

  @Test
  @DisplayName("correct url is called")
  void test_correct_url_is_called() {
    // given
    var baseUrl = "baseUrl";
    doReturn(baseUrl).when(spotifyConfig).getRestBaseUrl();
    var expectedUrl = baseUrl + SEARCH_ENDPOINT;
    SpotifyArtistSearchResultContainer responseMock = SpotifyArtistSearchResultContainerFactory.createDefault();
    doReturn(ResponseEntity.ok(responseMock)).when(restTemplate).exchange(any(), any(), any(), ArgumentMatchers.<Class<SpotifyArtistSearchResultContainer>>any(), any(), any());

    // when
    underTest.searchByName("token", "query", 1, 10);

    // then
    verify(restTemplate, times(1)).exchange(eq(expectedUrl), any(), any(), ArgumentMatchers.<Class<SpotifyArtistSearchResultContainer>>any(), any(), any());
  }

  @Test
  @DisplayName("a GET call is made")
  void test_correct_http_method() {
    // given
    SpotifyArtistSearchResultContainer responseMock = SpotifyArtistSearchResultContainerFactory.createDefault();
    doReturn(ResponseEntity.ok(responseMock)).when(restTemplate).exchange(any(), any(), any(), ArgumentMatchers.<Class<SpotifyArtistSearchResultContainer>>any(), any(), any());

    // when
    underTest.searchByName("token", "query", 1, 10);

    // then
    verify(restTemplate, times(1)).exchange(any(), eq(GET), any(), ArgumentMatchers.<Class<SpotifyArtistSearchResultContainer>>any(), any(), any());
  }

  @Test
  @DisplayName("correct standard headers are set")
  void test_correct_standard_headers() {
    // given
    SpotifyArtistSearchResultContainer responseMock = SpotifyArtistSearchResultContainerFactory.createDefault();
    doReturn(ResponseEntity.ok(responseMock)).when(restTemplate).exchange(any(), any(), any(), ArgumentMatchers.<Class<SpotifyArtistSearchResultContainer>>any(), any(), any());

    // when
    underTest.searchByName("token", "query", 1, 10);

    // then
    verify(restTemplate, times(1)).exchange(any(), any(), argumentCaptor.capture(), ArgumentMatchers.<Class<SpotifyArtistSearchResultContainer>>any(), any(), any());
    HttpEntity<Object> httpEntity = argumentCaptor.getValue();
    HttpHeaders headers = httpEntity.getHeaders();
    assertThat(headers.getAccept()).isEqualTo(Collections.singletonList(MediaType.APPLICATION_JSON));
    assertThat(headers.getAcceptCharset()).isEqualTo(Collections.singletonList(Charset.defaultCharset()));
  }

  @Test
  @DisplayName("token is set  with prefix in authorization header")
  void test_correct_authorization_header() {
    // given
    var token = "token";
    SpotifyArtistSearchResultContainer responseMock = SpotifyArtistSearchResultContainerFactory.createDefault();
    doReturn(ResponseEntity.ok(responseMock)).when(restTemplate).exchange(any(), any(), any(), ArgumentMatchers.<Class<SpotifyArtistSearchResultContainer>>any(), any(), any());

    // when
    underTest.searchByName(token, "query", 1, 10);

    // then
    verify(restTemplate, times(1)).exchange(any(), any(), argumentCaptor.capture(), ArgumentMatchers.<Class<SpotifyArtistSearchResultContainer>>any(), any(), any());
    HttpEntity<Object> httpEntity = argumentCaptor.getValue();
    HttpHeaders headers = httpEntity.getHeaders();
    assertThat(headers.get(AUTHORIZATION)).isEqualTo(Collections.singletonList(AUTHORIZATION_HEADER_PREFIX + token));
  }

  @Test
  @DisplayName("correct response type is requested")
  void test_correct_response_type() {
    // given
    SpotifyArtistSearchResultContainer responseMock = SpotifyArtistSearchResultContainerFactory.createDefault();
    doReturn(ResponseEntity.ok(responseMock)).when(restTemplate).exchange(any(), any(), any(), ArgumentMatchers.<Class<SpotifyArtistSearchResultContainer>>any(), any(), any());

    // when
    underTest.searchByName("token", "query", 1, 10);

    // then
    verify(restTemplate, times(1)).exchange(any(), any(), any(), eq(SpotifyArtistSearchResultContainer.class), any(), any());
  }

  @Test
  @DisplayName("query is url-encoded")
  void test_encoded_query() {
    // given
    var query = "i'm a query";
    var expectedQuery = "i%27m+a+query";
    SpotifyArtistSearchResultContainer responseMock = SpotifyArtistSearchResultContainerFactory.createDefault();
    doReturn(ResponseEntity.ok(responseMock)).when(restTemplate).exchange(any(), any(), any(), ArgumentMatchers.<Class<SpotifyArtistSearchResultContainer>>any(), any(), any());

    // when
    underTest.searchByName("token", query, 1, 10);

    // then
    verify(restTemplate, times(1)).exchange(any(), any(), any(), ArgumentMatchers.<Class<SpotifyArtistSearchResultContainer>>any(), eq(expectedQuery), any());
  }

  @ParameterizedTest(name = "for pageNumber = {0} and pageSize = {1} offset is = {2}")
  @MethodSource("offsetProvider")
  @DisplayName("offset is calculated and set")
  void test_offset(int pageNumber, int pageSize, int expectedOffset) {
    // given
    SpotifyArtistSearchResultContainer responseMock = SpotifyArtistSearchResultContainerFactory.createDefault();
    doReturn(ResponseEntity.ok(responseMock)).when(restTemplate).exchange(any(), any(), any(), ArgumentMatchers.<Class<SpotifyArtistSearchResultContainer>>any(), any(), any());

    // when
    underTest.searchByName("token", "query", pageNumber, pageSize);

    // then
    verify(restTemplate, times(1)).exchange(any(), any(), any(), ArgumentMatchers.<Class<SpotifyArtistSearchResultContainer>>any(), any(), eq(expectedOffset));
  }

  @Test
  @DisplayName("response is returned")
  void test_response() {
    // given
    SpotifyArtistSearchResultContainer responseMock = SpotifyArtistSearchResultContainerFactory.createDefault();
    doReturn(ResponseEntity.ok(responseMock)).when(restTemplate).exchange(any(), any(), any(), ArgumentMatchers.<Class<SpotifyArtistSearchResultContainer>>any(), any(), any());

    // when
    SpotifyArtistSearchResultContainer result = underTest.searchByName("token", "query", 1, 10);

    // then
    assertThat(result).isEqualTo(responseMock);
  }

  @Test
  @DisplayName("if the response is null, an ExternalServiceException is thrown")
  void test_exception_if_response_is_null() {
    // given
    doReturn(ResponseEntity.ok(null)).when(restTemplate).exchange(any(), any(), any(), ArgumentMatchers.<Class<SpotifyArtistSearchResultContainer>>any(), any(), any());

    // when
    Throwable throwable = catchThrowable(() -> underTest.searchByName("token", "query", 1, 10));

    // then
    assertThat(throwable).isInstanceOf(ExternalServiceException.class);
  }

  @ParameterizedTest(name = "if the status is {0}, an ExternalServiceException is thrown")
  @MethodSource("httpStatusCodeProvider")
  @DisplayName("if the status code is not OK, an ExternalServiceException is thrown")
  void test_exception_if_status_is_not_ok(HttpStatus httpStatus) {
    // given
    SpotifyArtistSearchResultContainer responseMock = SpotifyArtistSearchResultContainerFactory.createDefault();
    doReturn(ResponseEntity.status(httpStatus).body(responseMock)).when(restTemplate).exchange(any(), any(), any(), ArgumentMatchers.<Class<SpotifyArtistSearchResultContainer>>any(), any(), any());

    // when
    Throwable throwable = catchThrowable(() -> underTest.searchByName("token", "query", 1, 10));

    // then
    assertThat(throwable).isInstanceOf(ExternalServiceException.class);
  }

  private static Stream<Arguments> invalidQueryInputProvider() {
    return Stream.of(
        Arguments.of((Object) null),
        Arguments.of("")
    );
  }

  private static Stream<Arguments> offsetProvider() {
    return Stream.of(
        Arguments.of(1, 10, 0),
        Arguments.of(1, 15, 0),
        Arguments.of(2, 10, 10),
        Arguments.of(3, 15, 30)
    );
  }

  private static Stream<Arguments> httpStatusCodeProvider() {
    return Stream.of(HttpStatus.values()).filter(status -> !status.is2xxSuccessful()).map(Arguments::of);
  }
}