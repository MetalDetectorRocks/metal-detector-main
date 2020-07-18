package rocks.metaldetector.discogs.client;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import rocks.metaldetector.discogs.api.DiscogsArtist;
import rocks.metaldetector.discogs.api.DiscogsArtistSearchResultContainer;
import rocks.metaldetector.discogs.client.DiscogsDtoFactory.DiscogsArtistFactory;
import rocks.metaldetector.discogs.config.DiscogsConfig;
import rocks.metaldetector.support.exceptions.ExternalServiceException;

import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static rocks.metaldetector.discogs.client.DiscogsArtistSearchRestClientImpl.ARTIST_ID_SEARCH_URL_FRAGMENT;
import static rocks.metaldetector.discogs.client.DiscogsArtistSearchRestClientImpl.ARTIST_NAME_SEARCH_URL_FRAGMENT;
import static rocks.metaldetector.discogs.client.DiscogsDtoFactory.DiscogsArtistSearchResultContainerFactory;

@ExtendWith(MockitoExtension.class)
class DiscogsArtistSearchRestClientTest implements WithAssertions {

  @Mock
  private DiscogsConfig discogsConfig;

  @Mock
  private RestTemplate restTemplate;

  @InjectMocks
  private DiscogsArtistSearchRestClientImpl underTest;

  @AfterEach
  void tearDown() {
    reset(restTemplate, discogsConfig);
  }

  @Nested
  @TestInstance(Lifecycle.PER_CLASS)
  @DisplayName("Tests for method searchByName()")
  class SearchByNameTest {

    @ParameterizedTest(name = "An empty response is returned when query string is [{0}].")
    @MethodSource("nullOrEmptyQueryProvider")
    @DisplayName("An empty response is returned on null or empty query string.")
    void test_invalid_query_string(String queryString) {
      // when
      var response = underTest.searchByName(queryString, 1, 1);

      // then
      assertThat(response).isEqualTo(DiscogsArtistSearchResultContainerFactory.withEmptyResult());
    }

    private Stream<Arguments> nullOrEmptyQueryProvider() {
      return Stream.of(
              Arguments.of((String) null),
              Arguments.of("")
      );
    }

    @Test
    @DisplayName("A GET call is made on Discogs search url.")
    void test_get_on_search_url() {
      // given
      var discogsBaseUrl = "discogs-url";
      doReturn(discogsBaseUrl).when(discogsConfig).getRestBaseUrl();
      var expectedSearchUrl = discogsBaseUrl + ARTIST_NAME_SEARCH_URL_FRAGMENT;
      DiscogsArtistSearchResultContainer responseMock = DiscogsArtistSearchResultContainerFactory.createDefault();
      doReturn(ResponseEntity.ok(responseMock)).when(restTemplate).getForEntity(any(), any(), any(), any(), any());

      // when
      underTest.searchByName("query", 1, 1);

      // then
      verify(restTemplate, times(1)).getForEntity(eq(expectedSearchUrl), any(), any(), any(), any());
    }

    @Test
    @DisplayName("The provided query string is sent as url parameter.")
    void test_query_as_url_parameter() {
      // given
      var query = "the search query";
      DiscogsArtistSearchResultContainer responseMock = DiscogsArtistSearchResultContainerFactory.createDefault();
      doReturn(ResponseEntity.ok(responseMock)).when(restTemplate).getForEntity(any(), any(), any(), any(), any());

      // when
      underTest.searchByName(query, 1, 1);

      // then
      verify(restTemplate, times(1)).getForEntity(any(), any(), eq(query), any(), any());
    }

    @Test
    @DisplayName("The provided pagination info are sent as url parameters.")
    void test_pagination_as_url_parameters() {
      // given
      var page = 1;
      var size = 2;
      DiscogsArtistSearchResultContainer responseMock = DiscogsArtistSearchResultContainerFactory.createDefault();
      doReturn(ResponseEntity.ok(responseMock)).when(restTemplate).getForEntity(any(), any(), any(), any(), any());

      // when
      underTest.searchByName("query", page, size);

      // then
      verify(restTemplate, times(1)).getForEntity(any(), any(), any(), eq(page), eq(size));
    }

    @Test
    @DisplayName("The body of the result is returned")
    void test_response_is_returned() {
      // given
      DiscogsArtistSearchResultContainer responseMock = DiscogsArtistSearchResultContainerFactory.createDefault();
      doReturn(ResponseEntity.ok(responseMock)).when(restTemplate).getForEntity(any(), any(), any(), any(), any());

      // when
      var response = underTest.searchByName("query", 1, 1);

      // then
      assertThat(response).isEqualTo(responseMock);
    }

    @Test
    @DisplayName("If the response is null, a ExternalServiceException is thrown")
    void test_exception_if_response_is_null() {
      // given
      doReturn(ResponseEntity.ok(null)).when(restTemplate).getForEntity(any(), any(), any(), any(), any());

      // when
      Throwable throwable = catchThrowable(() -> underTest.searchByName("query", 1, 1));

      // then
      assertThat(throwable).isInstanceOf(ExternalServiceException.class);
    }

    @ParameterizedTest(name = "If the status is {0}, a ExternalServiceException is thrown")
    @MethodSource("httpStatusCodeProvider")
    @DisplayName("If the status code is not OK, a ExternalServiceException is thrown")
    void test_exception_if_status_is_not_ok(HttpStatus httpStatus) {
      // given
      DiscogsArtistSearchResultContainer responseMock = DiscogsArtistSearchResultContainerFactory.createDefault();
      doReturn(ResponseEntity.status(httpStatus).body(responseMock)).when(restTemplate).getForEntity(any(), any(), any(), any(), any());

      // when
      Throwable throwable = catchThrowable(() -> underTest.searchByName("query", 1, 1));

      // then
      assertThat(throwable).isInstanceOf(ExternalServiceException.class);
    }

    private Stream<Arguments> httpStatusCodeProvider() {
      return Stream.of(HttpStatus.values()).filter(status -> !status.is2xxSuccessful()).map(Arguments::of);
    }
  }

  @Nested
  @TestInstance(Lifecycle.PER_CLASS)
  @DisplayName("Tests for method searchById()")
  class SearchByIdTest {

    @ParameterizedTest(name = "An IllegalArgumentException is thrown when artistId is [{0}].")
    @MethodSource("illegalExternalIdProvider")
    @DisplayName("An IllegalArgumentException is thrown when artistId is blank.")
    void test_invalid_artist_id(String externalId) {
      // when
      var throwable = catchThrowable(() -> underTest.searchById(externalId));

      // then
      assertThat(throwable).isInstanceOf(IllegalArgumentException.class);
    }

    private Stream<Arguments> illegalExternalIdProvider() {
      return Stream.of(
              Arguments.of(""),
              Arguments.of("   ")
      );
    }

    @Test
    @DisplayName("A GET call is made on Discogs artist search url.")
    void test_get_on_artist_search_url() {
      // given
      var discogsBaseUrl = "discogs-url";
      doReturn(discogsBaseUrl).when(discogsConfig).getRestBaseUrl();
      var expectedSearchUrl = discogsBaseUrl + ARTIST_ID_SEARCH_URL_FRAGMENT;
      DiscogsArtist responseMock = DiscogsArtistFactory.createDefault();
      doReturn(ResponseEntity.ok(responseMock)).when(restTemplate).getForEntity(any(), any(), anyString());

      // when
      underTest.searchById("123");

      // then
      verify(restTemplate, times(1)).getForEntity(eq(expectedSearchUrl), any(), anyString());
    }

    @Test
    @DisplayName("The provided artistId is sent as url parameter.")
    void test_artist_id_as_url_parameter() {
      // given
      var artistId = "123";
      DiscogsArtist responseMock = DiscogsArtistFactory.createDefault();
      doReturn(ResponseEntity.ok(responseMock)).when(restTemplate).getForEntity(any(), any(), anyString());

      // when
      underTest.searchById(artistId);

      // then
      verify(restTemplate, times(1)).getForEntity(any(), any(), eq(artistId));
    }

    @Test
    @DisplayName("The body of the result is returned")
    void test_response_is_returned() {
      // given
      DiscogsArtist responseMock = DiscogsArtistFactory.createDefault();
      doReturn(ResponseEntity.ok(responseMock)).when(restTemplate).getForEntity(any(), any(), anyString());

      // when
      var response = underTest.searchById("123");

      // then
      assertThat(response).isEqualTo(responseMock);
    }

    @Test
    @DisplayName("If the response is null, a ExternalServiceException is thrown")
    void test_exception_if_response_is_null() {
      // given
      doReturn(ResponseEntity.ok(null)).when(restTemplate).getForEntity(any(), any(), anyString());

      // when
      Throwable throwable = catchThrowable(() -> underTest.searchById("123"));

      // then
      assertThat(throwable).isInstanceOf(ExternalServiceException.class);
    }

    @ParameterizedTest(name = "If the status is {0}, a ExternalServiceException is thrown")
    @MethodSource("httpStatusCodeProvider")
    @DisplayName("If the status code is not OK, a ExternalServiceException is thrown")
    void test_exception_if_status_is_not_ok(HttpStatus httpStatus) {
      // given
      DiscogsArtist responseMock = DiscogsArtistFactory.createDefault();
      doReturn(ResponseEntity.status(httpStatus).body(responseMock)).when(restTemplate).getForEntity(any(), any(), anyString());

      // when
      Throwable throwable = catchThrowable(() -> underTest.searchById("123"));

      // then
      assertThat(throwable).isInstanceOf(ExternalServiceException.class);
    }

    private Stream<Arguments> httpStatusCodeProvider() {
      return Stream.of(HttpStatus.values()).filter(status -> !status.is2xxSuccessful()).map(Arguments::of);
    }
  }
}
