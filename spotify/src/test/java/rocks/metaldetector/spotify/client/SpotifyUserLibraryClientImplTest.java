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
import rocks.metaldetector.spotify.api.imports.SpotifyAlbumImportResult;
import rocks.metaldetector.spotify.api.imports.SpotifyArtistImportResult;
import rocks.metaldetector.spotify.api.imports.SpotifyArtistImportResultContainer;
import rocks.metaldetector.spotify.api.search.SpotifyArtistSearchResultContainer;
import rocks.metaldetector.spotify.config.SpotifyProperties;
import rocks.metaldetector.support.exceptions.ExternalServiceException;

import java.nio.charset.Charset;
import java.util.Collections;
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
import static rocks.metaldetector.spotify.client.SpotifyUserLibraryClientImpl.GET_FOLLOWED_ARTISTS_ENDPOINT;
import static rocks.metaldetector.spotify.client.SpotifyUserLibraryClientImpl.GET_MY_ALBUMS_ENDPOINT;
import static rocks.metaldetector.spotify.client.SpotifyUserLibraryClientImpl.LIMIT;
import static rocks.metaldetector.spotify.client.SpotifyUserLibraryClientImpl.LIMIT_PARAMETER_NAME;
import static rocks.metaldetector.spotify.client.SpotifyUserLibraryClientImpl.OFFSET_PARAMETER_NAME;

@ExtendWith(MockitoExtension.class)
class SpotifyUserLibraryClientImplTest implements WithAssertions {

  private static final String AUTHORIZATION_HEADER_PREFIX = "Bearer ";

  @Mock
  private RestTemplate restTemplate;

  @Mock
  private SpotifyProperties spotifyProperties;

  private SpotifyUserLibraryClientImpl underTest;

  @Captor
  private ArgumentCaptor<HttpEntity<Object>> httpEntityCaptor;

  @Captor
  private ArgumentCaptor<Map<String, Object>> urlParameterCaptor;

  @BeforeEach
  void setup() {
    underTest = new SpotifyUserLibraryClientImpl(restTemplate, spotifyProperties);
  }

  @AfterEach
  void tearDown() {
    reset(restTemplate, spotifyProperties);
  }

  @DisplayName("Test for fetching liked albums")
  @TestInstance(TestInstance.Lifecycle.PER_CLASS)
  @Nested
  class FetchAlbumsTest {

    @ParameterizedTest(name = "if the token is {0} an IllegalArgumentException is thrown")
    @MethodSource("emptyTokenProvider")
    @DisplayName("exception is thrown on faulty token")
    void test_exception_on_empty_token(String token) {
      // when
      Throwable throwable = catchThrowable(() -> underTest.fetchLikedAlbums(token, 666));

      // then
      assertThat(throwable).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("correct url is called")
    void test_correct_url_called() {
      // given
      var baseUrl = "url";
      doReturn(baseUrl).when(spotifyProperties).getRestBaseUrl();
      doReturn(ResponseEntity.ok(new SpotifyAlbumImportResult())).when(restTemplate).exchange(any(), any(), any(), ArgumentMatchers.<Class<SpotifyAlbumImportResult>>any(), anyMap());

      // when
      underTest.fetchLikedAlbums("token", 666);

      // then
      verify(restTemplate).exchange(eq(baseUrl + GET_MY_ALBUMS_ENDPOINT), any(), any(), ArgumentMatchers.<Class<SpotifyAlbumImportResult>>any(), anyMap());
    }

    @Test
    @DisplayName("spotifyProperties are called to get base url")
    void test_spotify_properties_called() {
      // given
      doReturn(ResponseEntity.ok(new SpotifyAlbumImportResult())).when(restTemplate).exchange(any(), any(), any(), ArgumentMatchers.<Class<SpotifyArtistSearchResultContainer>>any(), anyMap());

      // when
      underTest.fetchLikedAlbums("token", 666);

      // then
      verify(spotifyProperties).getRestBaseUrl();
    }

    @Test
    @DisplayName("get call is made")
    void test_get_call_made() {
      // given
      doReturn(ResponseEntity.ok(new SpotifyAlbumImportResult())).when(restTemplate).exchange(any(), any(), any(), ArgumentMatchers.<Class<SpotifyAlbumImportResult>>any(), anyMap());

      // when
      underTest.fetchLikedAlbums("token", 666);

      // then
      verify(restTemplate).exchange(any(), eq(GET), any(), ArgumentMatchers.<Class<SpotifyAlbumImportResult>>any(), anyMap());
    }

    @Test
    @DisplayName("correct standard headers are set")
    void test_correct_standard_headers() {
      // given
      doReturn(ResponseEntity.ok(new SpotifyAlbumImportResult())).when(restTemplate).exchange(any(), any(), any(), ArgumentMatchers.<Class<SpotifyArtistSearchResultContainer>>any(), anyMap());

      // when
      underTest.fetchLikedAlbums("token", 666);

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
      doReturn(ResponseEntity.ok(new SpotifyAlbumImportResult())).when(restTemplate).exchange(any(), any(), any(), ArgumentMatchers.<Class<SpotifyArtistSearchResultContainer>>any(), anyMap());

      // when
      underTest.fetchLikedAlbums(token, 666);
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
      doReturn(ResponseEntity.ok(new SpotifyAlbumImportResult())).when(restTemplate).exchange(any(), any(), any(), ArgumentMatchers.<Class<SpotifyArtistSearchResultContainer>>any(), anyMap());

      // when
      underTest.fetchLikedAlbums("token", 666);

      // then
      verify(restTemplate).exchange(any(), any(), any(), eq(SpotifyAlbumImportResult.class), anyMap());
    }

    @Test
    @DisplayName("url parameters are set")
    void test_url_parameters() {
      // given
      var offset = 666;
      doReturn(ResponseEntity.ok(new SpotifyAlbumImportResult())).when(restTemplate).exchange(any(), any(), any(), ArgumentMatchers.<Class<SpotifyArtistSearchResultContainer>>any(), anyMap());

      // when
      underTest.fetchLikedAlbums("token", offset);

      // then
      verify(restTemplate).exchange(any(), any(), any(), ArgumentMatchers.<Class<SpotifyArtistSearchResultContainer>>any(), urlParameterCaptor.capture());
      Map<String, Object> urlParameter = urlParameterCaptor.getValue();
      assertThat(urlParameter.get(LIMIT_PARAMETER_NAME)).isEqualTo(LIMIT);
      assertThat(urlParameter.get(OFFSET_PARAMETER_NAME)).isEqualTo(offset);
    }

    @Test
    @DisplayName("ExternalStatusException is thrown if response body is null")
    void test_response_body_null() {
      // given
      doReturn(ResponseEntity.ok(null)).when(restTemplate).exchange(any(), any(), any(), ArgumentMatchers.<Class<SpotifyArtistSearchResultContainer>>any(), anyMap());

      // when
      Throwable throwable = catchThrowable(() -> underTest.fetchLikedAlbums("token", 666));

      // then
      assertThat(throwable).isInstanceOf(ExternalServiceException.class);
    }

    @ParameterizedTest(name = "If the status is {0}, an ExternalServiceException is thrown")
    @MethodSource("httpStatusCodeProvider")
    @DisplayName("If the status code is not OK an ExternalServiceException is thrown")
    void test_status_code_not_ok(HttpStatus httpStatus) {
      // given
      doReturn(ResponseEntity.status(httpStatus).body(new SpotifyAlbumImportResult())).when(restTemplate).exchange(any(), any(), any(), ArgumentMatchers.<Class<SpotifyArtistSearchResultContainer>>any(), anyMap());

      Throwable throwable = catchThrowable(() -> underTest.fetchLikedAlbums("token", 666));

      // then
      assertThat(throwable).isInstanceOf(ExternalServiceException.class);
    }

    @Test
    @DisplayName("result is returned")
    void test_result_returned() {
      // given
      var expectedResult = new SpotifyAlbumImportResult();
      doReturn(ResponseEntity.ok(expectedResult)).when(restTemplate).exchange(any(), any(), any(), ArgumentMatchers.<Class<SpotifyArtistSearchResultContainer>>any(), anyMap());

      // when
      var result = underTest.fetchLikedAlbums("token", 666);

      // then
      assertThat(result).isEqualTo(expectedResult);
    }

    private Stream<Arguments> emptyTokenProvider() {
      return Stream.of(
          Arguments.of((Object) null),
          Arguments.of("")
      );
    }

    private Stream<Arguments> httpStatusCodeProvider() {
      return Stream.of(HttpStatus.values()).filter(status -> !status.is2xxSuccessful()).map(Arguments::of);
    }
  }

  @DisplayName("Test for fetching followed artists")
  @TestInstance(TestInstance.Lifecycle.PER_CLASS)
  @Nested
  class FetchArtistsTest {

    @ParameterizedTest(name = "if the token is {0} an IllegalArgumentException is thrown")
    @MethodSource("emptyTokenProvider")
    @DisplayName("exception is thrown on faulty token")
    void test_exception_on_empty_token(String token) {
      // when
      Throwable throwable = catchThrowable(() -> underTest.fetchFollowedArtists(token, 666));

      // then
      assertThat(throwable).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("correct url is called")
    void test_correct_url_called() {
      // given
      var baseUrl = "url";
      doReturn(baseUrl).when(spotifyProperties).getRestBaseUrl();
      doReturn(ResponseEntity.ok(new SpotifyArtistImportResultContainer())).when(restTemplate).exchange(any(), any(), any(), ArgumentMatchers.<Class<SpotifyAlbumImportResult>>any(), anyMap());

      // when
      underTest.fetchFollowedArtists("token", 666);

      // then
      verify(restTemplate).exchange(eq(baseUrl + GET_FOLLOWED_ARTISTS_ENDPOINT), any(), any(), ArgumentMatchers.<Class<SpotifyAlbumImportResult>>any(), anyMap());
    }

    @Test
    @DisplayName("spotifyProperties are called to get base url")
    void test_spotify_properties_called() {
      // given
      doReturn(ResponseEntity.ok(new SpotifyArtistImportResultContainer())).when(restTemplate).exchange(any(), any(), any(), ArgumentMatchers.<Class<SpotifyArtistSearchResultContainer>>any(), anyMap());

      // when
      underTest.fetchFollowedArtists("token", 666);

      // then
      verify(spotifyProperties).getRestBaseUrl();
    }

    @Test
    @DisplayName("get call is made")
    void test_get_call_made() {
      // given
      doReturn(ResponseEntity.ok(new SpotifyArtistImportResultContainer())).when(restTemplate).exchange(any(), any(), any(), ArgumentMatchers.<Class<SpotifyAlbumImportResult>>any(), anyMap());

      // when
      underTest.fetchFollowedArtists("token", 666);

      // then
      verify(restTemplate).exchange(any(), eq(GET), any(), ArgumentMatchers.<Class<SpotifyAlbumImportResult>>any(), anyMap());
    }

    @Test
    @DisplayName("correct standard headers are set")
    void test_correct_standard_headers() {
      // given
      doReturn(ResponseEntity.ok(new SpotifyArtistImportResultContainer())).when(restTemplate).exchange(any(), any(), any(), ArgumentMatchers.<Class<SpotifyArtistSearchResultContainer>>any(), anyMap());

      // when
      underTest.fetchFollowedArtists("token", 666);

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
      doReturn(ResponseEntity.ok(new SpotifyArtistImportResultContainer())).when(restTemplate).exchange(any(), any(), any(), ArgumentMatchers.<Class<SpotifyArtistSearchResultContainer>>any(), anyMap());

      // when
      underTest.fetchFollowedArtists(token, 666);
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
      doReturn(ResponseEntity.ok(new SpotifyArtistImportResultContainer())).when(restTemplate).exchange(any(), any(), any(), ArgumentMatchers.<Class<SpotifyArtistSearchResultContainer>>any(), anyMap());

      // when
      underTest.fetchFollowedArtists("token", 666);

      // then
      verify(restTemplate).exchange(any(), any(), any(), eq(SpotifyArtistImportResultContainer.class), anyMap());
    }

    @Test
    @DisplayName("url parameters are set")
    void test_url_parameters() {
      // given
      var offset = 666;
      doReturn(ResponseEntity.ok(new SpotifyArtistImportResultContainer())).when(restTemplate).exchange(any(), any(), any(), ArgumentMatchers.<Class<SpotifyArtistSearchResultContainer>>any(), anyMap());

      // when
      underTest.fetchFollowedArtists("token", offset);

      // then
      verify(restTemplate).exchange(any(), any(), any(), ArgumentMatchers.<Class<SpotifyArtistSearchResultContainer>>any(), urlParameterCaptor.capture());
      Map<String, Object> urlParameter = urlParameterCaptor.getValue();
      assertThat(urlParameter.get(LIMIT_PARAMETER_NAME)).isEqualTo(LIMIT);
      assertThat(urlParameter.get(OFFSET_PARAMETER_NAME)).isEqualTo(offset);
    }

    @Test
    @DisplayName("ExternalStatusException is thrown if response body is null")
    void test_response_body_null() {
      // given
      doReturn(ResponseEntity.ok(null)).when(restTemplate).exchange(any(), any(), any(), ArgumentMatchers.<Class<SpotifyArtistSearchResultContainer>>any(), anyMap());

      // when
      Throwable throwable = catchThrowable(() -> underTest.fetchFollowedArtists("token", 666));

      // then
      assertThat(throwable).isInstanceOf(ExternalServiceException.class);
    }

    @ParameterizedTest(name = "If the status is {0}, an ExternalServiceException is thrown")
    @MethodSource("httpStatusCodeProvider")
    @DisplayName("If the status code is not OK an ExternalServiceException is thrown")
    void test_status_code_not_ok(HttpStatus httpStatus) {
      // given
      doReturn(ResponseEntity.status(httpStatus).body(new SpotifyArtistImportResultContainer())).when(restTemplate).exchange(any(), any(), any(), ArgumentMatchers.<Class<SpotifyArtistSearchResultContainer>>any(), anyMap());

      Throwable throwable = catchThrowable(() -> underTest.fetchFollowedArtists("token", 666));

      // then
      assertThat(throwable).isInstanceOf(ExternalServiceException.class);
    }

    @Test
    @DisplayName("result is returned")
    void test_result_returned() {
      // given
      var expectedResult = SpotifyArtistImportResultContainer.builder().artists(new SpotifyArtistImportResult()).build();
      doReturn(ResponseEntity.ok(expectedResult)).when(restTemplate).exchange(any(), any(), any(), ArgumentMatchers.<Class<SpotifyArtistSearchResultContainer>>any(), anyMap());

      // when
      var result = underTest.fetchFollowedArtists("token", 666);

      // then
      assertThat(result).isEqualTo(expectedResult.getArtists());
    }

    private Stream<Arguments> emptyTokenProvider() {
      return Stream.of(
          Arguments.of((Object) null),
          Arguments.of("")
      );
    }

    private Stream<Arguments> httpStatusCodeProvider() {
      return Stream.of(HttpStatus.values()).filter(status -> !status.is2xxSuccessful()).map(Arguments::of);
    }
  }
}