package rocks.metaldetector.spotify.client;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import rocks.metaldetector.spotify.api.SpotifyArtist;
import rocks.metaldetector.spotify.api.search.SpotifyArtistSearchResultContainer;
import rocks.metaldetector.spotify.api.search.SpotifyArtistsContainer;
import rocks.metaldetector.spotify.config.SpotifyProperties;
import rocks.metaldetector.support.exceptions.ExternalServiceException;

import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpMethod.GET;
import static rocks.metaldetector.spotify.client.SpotifyArtistSearchClientImpl.GET_ARTISTS_ENDPOINT;
import static rocks.metaldetector.spotify.client.SpotifyArtistSearchClientImpl.GET_ARTIST_ENDPOINT;
import static rocks.metaldetector.spotify.client.SpotifyArtistSearchClientImpl.ID_PARAMETER_NAME;
import static rocks.metaldetector.spotify.client.SpotifyArtistSearchClientImpl.LIMIT_PARAMETER_NAME;
import static rocks.metaldetector.spotify.client.SpotifyArtistSearchClientImpl.OFFSET_PARAMETER_NAME;
import static rocks.metaldetector.spotify.client.SpotifyArtistSearchClientImpl.SEARCH_ENDPOINT;
import static rocks.metaldetector.spotify.client.SpotifyDtoFactory.SpotfiyArtistFactory;
import static rocks.metaldetector.spotify.client.SpotifyDtoFactory.SpotifyArtistSearchResultContainerFactory;

@ExtendWith(MockitoExtension.class)
class SpotifyArtistSearchClientImplTest implements WithAssertions {

  private static final String AUTHORIZATION_HEADER_PREFIX = "Bearer ";

  @Mock
  private RestTemplate restTemplate;

  @Mock
  private SpotifyProperties spotifyProperties;

  private SpotifyArtistSearchClientImpl underTest;

  @Captor
  private ArgumentCaptor<HttpEntity<Object>> httpEntityCaptor;

  @Captor
  private ArgumentCaptor<Map<String, Object>> urlParameterCaptor;

  @BeforeEach
  void setup() {
    underTest = new SpotifyArtistSearchClientImpl(restTemplate, spotifyProperties);
  }

  @AfterEach
  void tearDown() {
    reset(restTemplate, spotifyProperties);
  }

  @Nested
  @TestInstance(TestInstance.Lifecycle.PER_CLASS)
  @DisplayName("Tests for method searchByName()")
  class SearchByNameTest {

    @ParameterizedTest(name = "empty result is returned for input <{0}>")
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
      doReturn(baseUrl).when(spotifyProperties).getRestBaseUrl();
      var expectedUrl = baseUrl + SEARCH_ENDPOINT;
      SpotifyArtistSearchResultContainer responseMock = SpotifyArtistSearchResultContainerFactory.createDefault();
      doReturn(ResponseEntity.ok(responseMock)).when(restTemplate).exchange(any(), any(), any(), ArgumentMatchers.<Class<SpotifyArtistSearchResultContainer>>any(), anyMap());

      // when
      underTest.searchByName("token", "query", 1, 10);

      // then
      verify(restTemplate).exchange(eq(expectedUrl), any(), any(), ArgumentMatchers.<Class<SpotifyArtistSearchResultContainer>>any(), anyMap());
    }

    @Test
    @DisplayName("a GET call is made")
    void test_correct_http_method() {
      // given
      SpotifyArtistSearchResultContainer responseMock = SpotifyArtistSearchResultContainerFactory.createDefault();
      doReturn(ResponseEntity.ok(responseMock)).when(restTemplate).exchange(any(), any(), any(), ArgumentMatchers.<Class<SpotifyArtistSearchResultContainer>>any(), anyMap());

      // when
      underTest.searchByName("token", "query", 1, 10);

      // then
      verify(restTemplate).exchange(any(), eq(GET), any(), ArgumentMatchers.<Class<SpotifyArtistSearchResultContainer>>any(), anyMap());
    }

    @Test
    @DisplayName("correct standard headers are set")
    void test_correct_standard_headers() {
      // given
      SpotifyArtistSearchResultContainer responseMock = SpotifyArtistSearchResultContainerFactory.createDefault();
      doReturn(ResponseEntity.ok(responseMock)).when(restTemplate).exchange(any(), any(), any(), ArgumentMatchers.<Class<SpotifyArtistSearchResultContainer>>any(), anyMap());

      // when
      underTest.searchByName("token", "query", 1, 10);

      // then
      verify(restTemplate).exchange(any(), any(), httpEntityCaptor.capture(), ArgumentMatchers.<Class<SpotifyArtistSearchResultContainer>>any(), anyMap());
      HttpEntity<Object> httpEntity = httpEntityCaptor.getValue();
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
      doReturn(ResponseEntity.ok(responseMock)).when(restTemplate).exchange(any(), any(), any(), ArgumentMatchers.<Class<SpotifyArtistSearchResultContainer>>any(), anyMap());

      // when
      underTest.searchByName(token, "query", 1, 10);

      // then
      verify(restTemplate).exchange(any(), any(), httpEntityCaptor.capture(), ArgumentMatchers.<Class<SpotifyArtistSearchResultContainer>>any(), anyMap());
      HttpEntity<Object> httpEntity = httpEntityCaptor.getValue();
      HttpHeaders headers = httpEntity.getHeaders();
      assertThat(headers.get(AUTHORIZATION)).isEqualTo(Collections.singletonList(AUTHORIZATION_HEADER_PREFIX + token));
    }

    @Test
    @DisplayName("correct response type is requested")
    void test_correct_response_type() {
      // given
      SpotifyArtistSearchResultContainer responseMock = SpotifyArtistSearchResultContainerFactory.createDefault();
      doReturn(ResponseEntity.ok(responseMock)).when(restTemplate).exchange(any(), any(), any(), ArgumentMatchers.<Class<SpotifyArtistSearchResultContainer>>any(), anyMap());

      // when
      underTest.searchByName("token", "query", 1, 10);

      // then
      verify(restTemplate).exchange(any(), any(), any(), eq(SpotifyArtistSearchResultContainer.class), anyMap());
    }

    @ParameterizedTest(name = "for pageNumber = {0} and pageSize = {1} offset is = {2}")
    @MethodSource("offsetProvider")
    @DisplayName("offset is calculated and set")
    void test_offset(int pageNumber, int pageSize, int expectedOffset) {
      // given
      SpotifyArtistSearchResultContainer responseMock = SpotifyArtistSearchResultContainerFactory.createDefault();
      doReturn(ResponseEntity.ok(responseMock)).when(restTemplate).exchange(any(), any(), any(), ArgumentMatchers.<Class<SpotifyArtistSearchResultContainer>>any(), anyMap());

      // when
      underTest.searchByName("token", "query", pageNumber, pageSize);

      // then
      verify(restTemplate).exchange(any(), any(), any(), ArgumentMatchers.<Class<SpotifyArtistSearchResultContainer>>any(), urlParameterCaptor.capture());
      Map<String, Object> urlParameter = urlParameterCaptor.getValue();
      assertThat(urlParameter.get(OFFSET_PARAMETER_NAME)).isEqualTo(expectedOffset);
    }

    @Test
    @DisplayName("limit is set")
    void test_limit() {
      // given
      var limit = 66;
      SpotifyArtistSearchResultContainer responseMock = SpotifyArtistSearchResultContainerFactory.createDefault();
      doReturn(ResponseEntity.ok(responseMock)).when(restTemplate).exchange(any(), any(), any(), ArgumentMatchers.<Class<SpotifyArtistSearchResultContainer>>any(), anyMap());

      // when
      underTest.searchByName("token", "query", 1, limit);

      // then
      verify(restTemplate).exchange(any(), any(), any(), ArgumentMatchers.<Class<SpotifyArtistSearchResultContainer>>any(), urlParameterCaptor.capture());
      Map<String, Object> urlParameter = urlParameterCaptor.getValue();
      assertThat(urlParameter.get(LIMIT_PARAMETER_NAME)).isEqualTo(limit);
    }

    @Test
    @DisplayName("response is returned")
    void test_response() {
      // given
      SpotifyArtistSearchResultContainer responseMock = SpotifyArtistSearchResultContainerFactory.createDefault();
      doReturn(ResponseEntity.ok(responseMock)).when(restTemplate).exchange(any(), any(), any(), ArgumentMatchers.<Class<SpotifyArtistSearchResultContainer>>any(), anyMap());

      // when
      SpotifyArtistSearchResultContainer result = underTest.searchByName("token", "query", 1, 10);

      // then
      assertThat(result).isEqualTo(responseMock);
    }

    @Test
    @DisplayName("if the response is null, an ExternalServiceException is thrown")
    void test_exception_if_response_is_null() {
      // given
      doReturn(ResponseEntity.ok(null)).when(restTemplate).exchange(any(), any(), any(), ArgumentMatchers.<Class<SpotifyArtistSearchResultContainer>>any(), anyMap());

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
      doReturn(ResponseEntity.status(httpStatus).body(responseMock)).when(restTemplate).exchange(any(), any(), any(), ArgumentMatchers.<Class<SpotifyArtistSearchResultContainer>>any(), anyMap());

      // when
      Throwable throwable = catchThrowable(() -> underTest.searchByName("token", "query", 1, 10));

      // then
      assertThat(throwable).isInstanceOf(ExternalServiceException.class);
    }

    private Stream<Arguments> invalidQueryInputProvider() {
      return Stream.of(
          Arguments.of((Object) null),
          Arguments.of("")
      );
    }

    private Stream<Arguments> offsetProvider() {
      return Stream.of(
          Arguments.of(1, 10, 0),
          Arguments.of(1, 15, 0),
          Arguments.of(2, 10, 10),
          Arguments.of(3, 15, 30)
      );
    }

    private Stream<Arguments> httpStatusCodeProvider() {
      return Stream.of(HttpStatus.values()).filter(status -> !status.is2xxSuccessful()).map(Arguments::of);
    }
  }

  @Nested
  @TestInstance(TestInstance.Lifecycle.PER_CLASS)
  @DisplayName("Tests for method searchById()")
  class SearchByIdTest {

    @ParameterizedTest(name = "If the artistId is <{0}>, an IllegalArgumentException is thrown")
    @MethodSource("invalidArtistIdProvider")
    @DisplayName("An IllegalArgumentException is thrown when artistId is invalid.")
    void test_invalid_artist_id(String artistId) {
      // when
      var throwable = catchThrowable(() -> underTest.searchById("token", artistId));

      // then
      assertThat(throwable).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Correct url is called")
    void test_correct_url_is_called() {
      // given
      var spotifyBaseUrl = "spotify-url";
      var expectedSearchUrl = spotifyBaseUrl + GET_ARTIST_ENDPOINT;
      var responseMock = SpotfiyArtistFactory.withArtistName("Slayer");
      doReturn(spotifyBaseUrl).when(spotifyProperties).getRestBaseUrl();
      doReturn(ResponseEntity.ok(responseMock)).when(restTemplate).exchange(any(), any(), any(), spotifyArtistClass(), anyMap());

      // when
      underTest.searchById("token", "666");

      // then
      verify(restTemplate).exchange(eq(expectedSearchUrl), any(), any(), spotifyArtistClass(), anyMap());
    }

    @Test
    @DisplayName("A GET call is made on Spotify artist search url")
    void test_get_on_artist_search_url() {
      // given
      var responseMock = SpotfiyArtistFactory.withArtistName("Slayer");
      doReturn(ResponseEntity.ok(responseMock)).when(restTemplate).exchange(any(), any(), any(), spotifyArtistClass(), anyMap());

      // when
      underTest.searchById("token", "666");

      // then
      verify(restTemplate).exchange(any(), eq(GET), any(), spotifyArtistClass(), anyMap());
    }

    @Test
    @DisplayName("correct standard headers are set")
    void test_correct_standard_headers() {
      // given
      var responseMock = SpotfiyArtistFactory.withArtistName("Slayer");
      doReturn(ResponseEntity.ok(responseMock)).when(restTemplate).exchange(any(), any(), any(), spotifyArtistClass(), anyMap());

      // when
      underTest.searchById("token", "666");

      // then
      verify(restTemplate).exchange(any(), any(), httpEntityCaptor.capture(), spotifyArtistClass(), anyMap());
      HttpEntity<Object> httpEntity = httpEntityCaptor.getValue();
      HttpHeaders headers = httpEntity.getHeaders();
      assertThat(headers.getAccept()).isEqualTo(Collections.singletonList(MediaType.APPLICATION_JSON));
      assertThat(headers.getAcceptCharset()).isEqualTo(Collections.singletonList(Charset.defaultCharset()));
    }

    @Test
    @DisplayName("token is set  with prefix in authorization header")
    void test_correct_authorization_header() {
      // given
      var token = "token";
      var responseMock = SpotfiyArtistFactory.withArtistName("Slayer");
      doReturn(ResponseEntity.ok(responseMock)).when(restTemplate).exchange(any(), any(), any(), spotifyArtistClass(), anyMap());

      // when
      underTest.searchById(token, "666");

      // then
      verify(restTemplate).exchange(any(), any(), httpEntityCaptor.capture(), ArgumentMatchers.<Class<SpotifyArtistSearchResultContainer>>any(), anyMap());
      HttpEntity<Object> httpEntity = httpEntityCaptor.getValue();
      HttpHeaders headers = httpEntity.getHeaders();
      assertThat(headers.get(AUTHORIZATION)).isEqualTo(Collections.singletonList(AUTHORIZATION_HEADER_PREFIX + token));
    }

    @Test
    @DisplayName("correct response type is requested")
    void test_correct_response_type() {
      // given
      var responseMock = SpotfiyArtistFactory.withArtistName("Slayer");
      doReturn(ResponseEntity.ok(responseMock)).when(restTemplate).exchange(any(), any(), any(), spotifyArtistClass(), anyMap());

      // when
      underTest.searchById("token", "666");

      // then
      verify(restTemplate).exchange(any(), any(), any(), eq(SpotifyArtist.class), anyMap());
    }

    @Test
    @DisplayName("The provided artistId is sent as url parameter.")
    void test_artist_id_as_url_parameter() {
      // given
      var artistId = "666";
      var responseMock = SpotfiyArtistFactory.withArtistName("Slayer");
      doReturn(ResponseEntity.ok(responseMock)).when(restTemplate).exchange(any(), any(), any(), spotifyArtistClass(), anyMap());

      // when
      underTest.searchById("token", artistId);

      // then
      verify(restTemplate).exchange(any(), any(), any(), spotifyArtistClass(), urlParameterCaptor.capture());
      Map<String, Object> urlParameter = urlParameterCaptor.getValue();
      assertThat(urlParameter.get(ID_PARAMETER_NAME)).isEqualTo(artistId);
    }

    @Test
    @DisplayName("response is returned")
    void test_response() {
      // given
      var responseMock = SpotfiyArtistFactory.withArtistName("Slayer");
      doReturn(ResponseEntity.ok(responseMock)).when(restTemplate).exchange(any(), any(), any(), spotifyArtistClass(), anyMap());

      // when
      var result = underTest.searchById("token", "666");

      // then
      assertThat(result).isEqualTo(responseMock);
    }

    @Test
    @DisplayName("If the response is null, an ExternalServiceException is thrown")
    void test_exception_if_response_is_null() {
      // given
      doReturn(ResponseEntity.ok(null)).when(restTemplate).exchange(any(), any(), any(), spotifyArtistClass(), anyMap());

      // when
      Throwable throwable = catchThrowable(() -> underTest.searchById("token", "666"));

      // then
      assertThat(throwable).isInstanceOf(ExternalServiceException.class);
    }

    @ParameterizedTest(name = "If the status is {0}, an ExternalServiceException is thrown")
    @MethodSource("httpStatusCodeProvider")
    @DisplayName("If the status code is not OK, an ExternalServiceException is thrown")
    void test_exception_if_status_is_not_ok(HttpStatus httpStatus) {
      // given
      var responseMock = SpotfiyArtistFactory.withArtistName("Slayer");
      doReturn(ResponseEntity.status(httpStatus).body(responseMock)).when(restTemplate).exchange(any(), any(), any(), spotifyArtistClass(), anyMap());

      // when
      Throwable throwable = catchThrowable(() -> underTest.searchById("token", "666"));

      // then
      assertThat(throwable).isInstanceOf(ExternalServiceException.class);
    }

    private Class<SpotifyArtist> spotifyArtistClass() {
      return ArgumentMatchers.any();
    }

    private Stream<Arguments> invalidArtistIdProvider() {
      return Stream.of(
          Arguments.of((Object) null),
          Arguments.of("")
      );
    }

    private Stream<Arguments> httpStatusCodeProvider() {
      return Stream.of(HttpStatus.values()).filter(status -> !status.is2xxSuccessful()).map(Arguments::of);
    }
  }

  @Nested
  @TestInstance(TestInstance.Lifecycle.PER_CLASS)
  @DisplayName("Tests for method searchByIds()")
  class SearchByIdsTest {

    @ParameterizedTest(name = "If the artistIds list is empty or null, an IllegalArgumentException is thrown")
    @MethodSource("invalidArtistIdProvider")
    @DisplayName("An IllegalArgumentException is thrown when artistIds list is invalid.")
    void test_invalid_artist_ids(List<String> artistIds) {
      // when
      var throwable = catchThrowable(() -> underTest.searchByIds("token", artistIds));

      // then
      assertThat(throwable).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Correct url is called")
    void test_correct_url_is_called() {
      // given
      var spotifyBaseUrl = "spotify-url";
      var expectedSearchUrl = spotifyBaseUrl + GET_ARTISTS_ENDPOINT;
      var responseMock = SpotifyArtistsContainer.builder()
          .artists(List.of(SpotfiyArtistFactory.withArtistName("Slayer")))
          .build();
      doReturn(spotifyBaseUrl).when(spotifyProperties).getRestBaseUrl();
      doReturn(ResponseEntity.ok(responseMock)).when(restTemplate).exchange(any(), any(), any(), spotifyArtistContainerClass(), anyMap());

      // when
      underTest.searchByIds("token", List.of("666"));

      // then
      verify(restTemplate).exchange(eq(expectedSearchUrl), any(), any(), spotifyArtistContainerClass(), anyMap());
    }

    @Test
    @DisplayName("A GET call is made on Spotify artists search url")
    void test_get_on_artists_search_url() {
      // given
      var responseMock = SpotifyArtistsContainer.builder()
          .artists(List.of(SpotfiyArtistFactory.withArtistName("Slayer")))
          .build();
      doReturn(ResponseEntity.ok(responseMock)).when(restTemplate).exchange(any(), any(), any(), spotifyArtistContainerClass(), anyMap());

      // when
      underTest.searchByIds("token", List.of("666"));

      // then
      verify(restTemplate).exchange(any(), eq(GET), any(), spotifyArtistContainerClass(), anyMap());
    }

    @Test
    @DisplayName("correct standard headers are set")
    void test_correct_standard_headers() {
      // given
      var responseMock = SpotifyArtistsContainer.builder()
          .artists(List.of(SpotfiyArtistFactory.withArtistName("Slayer")))
          .build();
      doReturn(ResponseEntity.ok(responseMock)).when(restTemplate).exchange(any(), any(), any(), spotifyArtistContainerClass(), anyMap());

      // when
      underTest.searchByIds("token", List.of("666"));

      // then
      verify(restTemplate).exchange(any(), any(), httpEntityCaptor.capture(), spotifyArtistContainerClass(), anyMap());
      HttpEntity<Object> httpEntity = httpEntityCaptor.getValue();
      HttpHeaders headers = httpEntity.getHeaders();
      assertThat(headers.getAccept()).isEqualTo(Collections.singletonList(MediaType.APPLICATION_JSON));
      assertThat(headers.getAcceptCharset()).isEqualTo(Collections.singletonList(Charset.defaultCharset()));
    }

    @Test
    @DisplayName("token is set  with prefix in authorization header")
    void test_correct_authorization_header() {
      // given
      var token = "token";
      var responseMock = SpotifyArtistsContainer.builder()
          .artists(List.of(SpotfiyArtistFactory.withArtistName("Slayer")))
          .build();
      doReturn(ResponseEntity.ok(responseMock)).when(restTemplate).exchange(any(), any(), any(), spotifyArtistContainerClass(), anyMap());

      // when
      underTest.searchByIds(token, List.of("666"));

      // then
      verify(restTemplate).exchange(any(), any(), httpEntityCaptor.capture(), ArgumentMatchers.<Class<SpotifyArtistSearchResultContainer>>any(), anyMap());
      HttpEntity<Object> httpEntity = httpEntityCaptor.getValue();
      HttpHeaders headers = httpEntity.getHeaders();
      assertThat(headers.get(AUTHORIZATION)).isEqualTo(Collections.singletonList(AUTHORIZATION_HEADER_PREFIX + token));
    }

    @Test
    @DisplayName("correct response type is requested")
    void test_correct_response_type() {
      // given
      var responseMock = SpotifyArtistsContainer.builder()
          .artists(List.of(SpotfiyArtistFactory.withArtistName("Slayer")))
          .build();
      doReturn(ResponseEntity.ok(responseMock)).when(restTemplate).exchange(any(), any(), any(), spotifyArtistContainerClass(), anyMap());

      // when
      underTest.searchByIds("token", List.of("666"));

      // then
      verify(restTemplate).exchange(any(), any(), any(), eq(SpotifyArtistsContainer.class), anyMap());
    }

    @Test
    @DisplayName("The provided artistIds are sent as url parameter.")
    void test_artist_ids_as_url_parameter() {
      // given
      var artistIds = List.of("555", "666");
      var responseMock = SpotifyArtistsContainer.builder()
          .artists(List.of(SpotfiyArtistFactory.withArtistName("Slayer")))
          .build();
      doReturn(ResponseEntity.ok(responseMock)).when(restTemplate).exchange(any(), any(), any(), spotifyArtistContainerClass(), anyMap());

      // when
      underTest.searchByIds("token", artistIds);

      // then
      verify(restTemplate).exchange(any(), any(), any(), spotifyArtistContainerClass(), urlParameterCaptor.capture());
      Map<String, Object> urlParameter = urlParameterCaptor.getValue();
      assertThat(urlParameter.get(ID_PARAMETER_NAME)).isEqualTo(String.join(",", artistIds));
    }

    @Test
    @DisplayName("response is returned")
    void test_response() {
      // given
      var responseMock = SpotifyArtistsContainer.builder()
          .artists(List.of(SpotfiyArtistFactory.withArtistName("Slayer")))
          .build();
      doReturn(ResponseEntity.ok(responseMock)).when(restTemplate).exchange(any(), any(), any(), spotifyArtistContainerClass(), anyMap());

      // when
      var result = underTest.searchByIds("token", List.of("666"));

      // then
      assertThat(result).isEqualTo(responseMock);
    }

    @Test
    @DisplayName("If the response is null, an ExternalServiceException is thrown")
    void test_exception_if_response_is_null() {
      // given
      doReturn(ResponseEntity.ok(null)).when(restTemplate).exchange(any(), any(), any(), spotifyArtistContainerClass(), anyMap());

      // when
      Throwable throwable = catchThrowable(() -> underTest.searchByIds("token", List.of("666")));

      // then
      assertThat(throwable).isInstanceOf(ExternalServiceException.class);
    }

    @ParameterizedTest(name = "If the status is {0}, an ExternalServiceException is thrown")
    @MethodSource("httpStatusCodeProvider")
    @DisplayName("If the status code is not OK, an ExternalServiceException is thrown")
    void test_exception_if_status_is_not_ok(HttpStatus httpStatus) {
      // given
      var responseMock = SpotifyArtistsContainer.builder()
          .artists(List.of(SpotfiyArtistFactory.withArtistName("Slayer")))
          .build();
      doReturn(ResponseEntity.status(httpStatus).body(responseMock)).when(restTemplate).exchange(any(), any(), any(), spotifyArtistContainerClass(), anyMap());

      // when
      Throwable throwable = catchThrowable(() -> underTest.searchByIds("token", List.of("666")));

      // then
      assertThat(throwable).isInstanceOf(ExternalServiceException.class);
    }

    private Class<SpotifyArtistsContainer> spotifyArtistContainerClass() {
      return ArgumentMatchers.any();
    }

    private Stream<Arguments> invalidArtistIdProvider() {
      return Stream.of(
          Arguments.of((Object) null),
          Arguments.of(Collections.emptyList())
      );
    }

    private Stream<Arguments> httpStatusCodeProvider() {
      return Stream.of(HttpStatus.values()).filter(status -> !status.is2xxSuccessful()).map(Arguments::of);
    }
  }
}
