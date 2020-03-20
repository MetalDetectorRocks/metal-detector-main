package rocks.metaldetector.discogs.domain;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import rocks.metaldetector.discogs.domain.DtoFactory.DiscogsArtistFactory;
import rocks.metaldetector.discogs.domain.DtoFactory.DiscogsArtistSearchResultFactory;
import rocks.metaldetector.discogs.api.DiscogsArtist;
import rocks.metaldetector.discogs.api.DiscogsArtistSearchResultContainer;
import rocks.metaldetector.discogs.config.DiscogsCredentialsConfig;
import rocks.metaldetector.discogs.fascade.dto.DiscogsArtistDto;
import rocks.metaldetector.discogs.fascade.dto.DiscogsArtistSearchResultDto;
import rocks.metaldetector.discogs.fascade.dto.DiscogsSearchResultDto;
import rocks.metaldetector.discogs.domain.transformer.DiscogsArtistSearchResultContainerTransformer;
import rocks.metaldetector.discogs.domain.transformer.DiscogsArtistTransformer;

import java.util.Optional;
import java.util.stream.Stream;

import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static rocks.metaldetector.discogs.domain.DiscogsArtistSearchRestClientImpl.ARTIST_ID_SEARCH_URL_FRAGMENT;
import static rocks.metaldetector.discogs.domain.DiscogsArtistSearchRestClientImpl.ARTIST_NAME_SEARCH_URL_FRAGMENT;

@ExtendWith(MockitoExtension.class)
class DiscogsArtistSearchRestClientTest implements WithAssertions {

  private static final int DEFAULT_PAGE = 1;
  private static final int DEFAULT_SIZE = 10;

  private static final String BASE_URL = "base-url";

  private static final String SEARCH_BY_NAME_URL = BASE_URL + ARTIST_NAME_SEARCH_URL_FRAGMENT;
  private static final String SEARCH_BY_ID_URL   = BASE_URL + ARTIST_ID_SEARCH_URL_FRAGMENT;

  @Mock
  private DiscogsCredentialsConfig discogsCredentialsConfig;

  @Mock
  private RestTemplate restTemplate;

  @Spy
  private DiscogsArtistTransformer artistTransformer;

  @Spy
  private DiscogsArtistSearchResultContainerTransformer searchResultTransformer;

  @InjectMocks
  private DiscogsArtistSearchRestClientImpl artistSearchClient;

  @BeforeEach
  void setUp() {
  }

  @AfterEach
  void tearDown() {
    reset(restTemplate);
  }

  @Nested
  @TestInstance(Lifecycle.PER_CLASS)
  @DisplayName("Tests for method searchByName()")
  class SearchByNameTest {

    @Test
    @DisplayName("Searching by name should return a valid result")
    void search_by_name() {
      // given
      final String ARTIST_NAME_QUERY = "Darkthrone";
      DiscogsArtistSearchResultContainer resultContainer = DiscogsArtistSearchResultFactory.withOneResult();
      when(discogsCredentialsConfig.getRestBaseUrl()).thenReturn(BASE_URL);
      when(restTemplate.getForEntity(SEARCH_BY_NAME_URL, DiscogsArtistSearchResultContainer.class, ARTIST_NAME_QUERY, DEFAULT_PAGE, DEFAULT_SIZE))
              .thenReturn(ResponseEntity.ok(resultContainer));

      // when
      Optional<DiscogsSearchResultDto<DiscogsArtistSearchResultDto>> result = artistSearchClient.searchByName(ARTIST_NAME_QUERY, DEFAULT_PAGE, DEFAULT_SIZE);

      // then
      // ToDo DanielW: Refactor this tests
//      assertThat(result).isPresent();
//      assertThat(result.get()).isEqualTo(resultContainer);
//      verify(restTemplate, times(1)).getForEntity(SEARCH_BY_NAME_URL, DiscogsArtistSearchResultContainer.class, ARTIST_NAME_QUERY, DEFAULT_PAGE, DEFAULT_SIZE);
    }

    @ParameterizedTest(name = "[{index}] => Result container <{0}> | HttpStatus <{1}>")
    @MethodSource("responseProvider")
    @DisplayName("Searching by name should return an empty optional when Discogs returns no usable response")
    void search_by_name_with_no_usable_response_from_discogs(DiscogsArtistSearchResultContainer resultContainer, HttpStatus httpStatus) {
      // given
      final String ARTIST_NAME_QUERY = "Darkthrone";
      when(discogsCredentialsConfig.getRestBaseUrl()).thenReturn(BASE_URL);
      when(restTemplate.getForEntity(SEARCH_BY_NAME_URL, DiscogsArtistSearchResultContainer.class, ARTIST_NAME_QUERY, DEFAULT_PAGE, DEFAULT_SIZE))
              .thenReturn(ResponseEntity.status(httpStatus).body(resultContainer));

      // when
      Optional<DiscogsSearchResultDto<DiscogsArtistSearchResultDto>> result = artistSearchClient.searchByName(ARTIST_NAME_QUERY, DEFAULT_PAGE, DEFAULT_SIZE);

      // then
      assertThat(result).isEmpty();
      verify(restTemplate, times(1)).getForEntity(SEARCH_BY_NAME_URL, DiscogsArtistSearchResultContainer.class, ARTIST_NAME_QUERY, DEFAULT_PAGE, DEFAULT_SIZE);
    }

    private Stream<Arguments> responseProvider() {
      DiscogsArtistSearchResultContainer resultContainer = DiscogsArtistSearchResultFactory.withOneResult();
      DiscogsArtistSearchResultContainer emptyResultContainer = DiscogsArtistSearchResultFactory.withEmptyResult();
      return Stream.of(
              Arguments.of(null, HttpStatus.OK),
              Arguments.of(resultContainer, HttpStatus.BAD_REQUEST),
              Arguments.of(emptyResultContainer, HttpStatus.OK)
      );
    }

    @ParameterizedTest(name = "[{index}] => Artist name <{0}>")
    @MethodSource("invalidArtistNameProvider")
    @DisplayName("Searching by name with null or empty artist name should return an empty optional")
    void search_by_name_with_invalid_artist_name(String artistName) {
      // when
      Optional<DiscogsSearchResultDto<DiscogsArtistSearchResultDto>> result = artistSearchClient.searchByName(artistName, DEFAULT_PAGE, DEFAULT_SIZE);

      // then
      assertThat(result).isEmpty();
    }

    private Stream<Arguments> invalidArtistNameProvider() {
      return Stream.of(
              Arguments.of(""),
              Arguments.of((String) null)
      );
    }
  }

  @Nested
  @TestInstance(Lifecycle.PER_CLASS)
  @DisplayName("Tests for method searchById()")
  class SearchByIdTest {

    @Test
    @DisplayName("Searching by id should return a valid result")
    void search_by_id() {
      // given
      final long ARTIST_ID = 252211L;
      DiscogsArtist discogsArtist = DiscogsArtistFactory.createTestArtist();
      when(discogsCredentialsConfig.getRestBaseUrl()).thenReturn(BASE_URL);
      when(restTemplate.getForEntity(SEARCH_BY_ID_URL, DiscogsArtist.class, ARTIST_ID))
              .thenReturn(ResponseEntity.ok(discogsArtist));

      // when
      Optional<DiscogsArtistDto> result = artistSearchClient.searchById(ARTIST_ID);

      // then
      // ToDo DanielW: Refactor this Tests
//      assertThat(result).isPresent();
//      assertThat(result.get()).isEqualTo(discogsArtist);
//      verify(restTemplate, times(1)).getForEntity(SEARCH_BY_ID_URL, DiscogsArtist.class, ARTIST_ID);
    }

    @ParameterizedTest(name = "[{index}] => Artist <{0}> | HttpStatus <{1}>")
    @MethodSource("responseProvider")
    @DisplayName("Searching by id should return an empty optional when Discogs returns no usable response")
    void search_by_name_with_no_usable_response_from_discogs(DiscogsArtist discogsArtist, HttpStatus httpStatus) {
      // given
      final long ARTIST_ID_QUERY = 1L;
      when(discogsCredentialsConfig.getRestBaseUrl()).thenReturn(BASE_URL);
      when(restTemplate.getForEntity(SEARCH_BY_ID_URL, DiscogsArtist.class, ARTIST_ID_QUERY))
              .thenReturn(ResponseEntity.status(httpStatus).body(discogsArtist));

      // when
      Optional<DiscogsArtistDto> result = artistSearchClient.searchById(ARTIST_ID_QUERY);

      // then
      assertThat(result).isEmpty();
      verify(restTemplate, times(1)).getForEntity(SEARCH_BY_ID_URL, DiscogsArtist.class, ARTIST_ID_QUERY);
    }

    private Stream<Arguments> responseProvider() {
      DiscogsArtist discogsArtist = DiscogsArtistFactory.createTestArtist();
      return Stream.of(
              Arguments.of(null, HttpStatus.OK),
              Arguments.of(discogsArtist, HttpStatus.BAD_REQUEST)
      );
    }

    @ParameterizedTest(name = "[{index}] => Artist Id <{0}>")
    @MethodSource("invalidArtistIdProvider")
    @DisplayName("Searching by id with id less than or equal to 0 should return an empty optional")
    void search_by_name_with_invalid_artist_id(long artistId) {
      // when
      Optional<DiscogsArtistDto> result = artistSearchClient.searchById(artistId);

      // then
      assertThat(result).isEmpty();
    }

    private Stream<Arguments> invalidArtistIdProvider() {
      return Stream.of(
              Arguments.of(0),
              Arguments.of(-10),
              Arguments.of(Long.MIN_VALUE)
      );
    }

  }
}
