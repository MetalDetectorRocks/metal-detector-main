package com.metalr2.web.controller.discogs;

import com.metalr2.config.misc.DiscogsConfig;
import com.metalr2.web.DtoFactory.ArtistFactory;
import com.metalr2.web.DtoFactory.ArtistSearchResultContainerFactory;
import com.metalr2.web.dto.discogs.artist.Artist;
import com.metalr2.web.dto.discogs.search.ArtistSearchResultContainer;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.*;
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

import java.util.Optional;
import java.util.stream.Stream;

import static com.metalr2.web.controller.discogs.ArtistSearchRestClient.ARTIST_ID_SEARCH_URL_FRAGMENT;
import static com.metalr2.web.controller.discogs.ArtistSearchRestClient.ARTIST_NAME_SEARCH_URL_FRAGMENT;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArtistSearchRestClientTest implements WithAssertions {

  private static final int DEFAULT_PAGE = 1;
  private static final int DEFAULT_SIZE = 10;

  private static final String BASE_URL = "base-url";

  private static final String SEARCH_BY_NAME_URL = BASE_URL + ARTIST_NAME_SEARCH_URL_FRAGMENT;
  private static final String SEARCH_BY_ID_URL   = BASE_URL + ARTIST_ID_SEARCH_URL_FRAGMENT;

  @Mock
  private DiscogsConfig discogsConfig;

  @Mock
  private RestTemplate restTemplate;

  @InjectMocks
  private ArtistSearchRestClient artistSearchClient;

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
      ArtistSearchResultContainer resultContainer = ArtistSearchResultContainerFactory.withOneResult();
      when(discogsConfig.getRestBaseUrl()).thenReturn(BASE_URL);
      when(restTemplate.getForEntity(SEARCH_BY_NAME_URL, ArtistSearchResultContainer.class, ARTIST_NAME_QUERY, DEFAULT_PAGE, DEFAULT_SIZE))
              .thenReturn(ResponseEntity.ok(resultContainer));

      // when
      Optional<ArtistSearchResultContainer> result = artistSearchClient.searchByName(ARTIST_NAME_QUERY, DEFAULT_PAGE, DEFAULT_SIZE);

      // then
      assertThat(result).isPresent();
      assertThat(result.get()).isEqualTo(resultContainer);
      verify(restTemplate, times(1)).getForEntity(SEARCH_BY_NAME_URL, ArtistSearchResultContainer.class, ARTIST_NAME_QUERY, DEFAULT_PAGE, DEFAULT_SIZE);
    }

    @ParameterizedTest(name = "[{index}] => Result container <{0}> | HttpStatus <{1}>")
    @MethodSource("responseProvider")
    @DisplayName("Searching by name should return an empty optional when Discogs returns no usable response")
    void search_by_name_with_no_usable_response_from_discogs(ArtistSearchResultContainer resultContainer, HttpStatus httpStatus) {
      // given
      final String ARTIST_NAME_QUERY = "Darkthrone";
      when(discogsConfig.getRestBaseUrl()).thenReturn(BASE_URL);
      when(restTemplate.getForEntity(SEARCH_BY_NAME_URL, ArtistSearchResultContainer.class, ARTIST_NAME_QUERY, DEFAULT_PAGE, DEFAULT_SIZE))
              .thenReturn(ResponseEntity.status(httpStatus).body(resultContainer));

      // when
      Optional<ArtistSearchResultContainer> result = artistSearchClient.searchByName(ARTIST_NAME_QUERY, DEFAULT_PAGE, DEFAULT_SIZE);

      // then
      assertThat(result).isEmpty();
      verify(restTemplate, times(1)).getForEntity(SEARCH_BY_NAME_URL, ArtistSearchResultContainer.class, ARTIST_NAME_QUERY, DEFAULT_PAGE, DEFAULT_SIZE);
    }

    private Stream<Arguments> responseProvider() {
      ArtistSearchResultContainer resultContainer = ArtistSearchResultContainerFactory.withOneResult();
      ArtistSearchResultContainer emptyResultContainer = ArtistSearchResultContainerFactory.withEmptyResult();
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
      Optional<ArtistSearchResultContainer> result = artistSearchClient.searchByName(artistName, DEFAULT_PAGE, DEFAULT_SIZE);

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
      final long ARTIST_ID = 1L;
      Artist artist = ArtistFactory.createTestArtist();
      when(discogsConfig.getRestBaseUrl()).thenReturn(BASE_URL);
      when(restTemplate.getForEntity(SEARCH_BY_ID_URL, Artist.class, ARTIST_ID))
              .thenReturn(ResponseEntity.ok(artist));

      // when
      Optional<Artist> result = artistSearchClient.searchById(ARTIST_ID);

      // then
      assertThat(result).isPresent();
      assertThat(result.get()).isEqualTo(artist);
      verify(restTemplate, times(1)).getForEntity(SEARCH_BY_ID_URL, Artist.class, ARTIST_ID);
    }

    @ParameterizedTest(name = "[{index}] => Artist <{0}> | HttpStatus <{1}>")
    @MethodSource("responseProvider")
    @DisplayName("Searching by id should return an empty optional when Discogs returns no usable response")
    void search_by_name_with_no_usable_response_from_discogs(Artist artist, HttpStatus httpStatus) {
      // given
      final long ARTIST_ID_QUERY = 1L;
      when(discogsConfig.getRestBaseUrl()).thenReturn(BASE_URL);
      when(restTemplate.getForEntity(SEARCH_BY_ID_URL, Artist.class, ARTIST_ID_QUERY))
              .thenReturn(ResponseEntity.status(httpStatus).body(artist));

      // when
      Optional<Artist> result = artistSearchClient.searchById(ARTIST_ID_QUERY);

      // then
      assertThat(result).isEmpty();
      verify(restTemplate, times(1)).getForEntity(SEARCH_BY_ID_URL, Artist.class, ARTIST_ID_QUERY);
    }

    private Stream<Arguments> responseProvider() {
      Artist artist = ArtistFactory.createTestArtist();
      return Stream.of(
              Arguments.of(null, HttpStatus.OK),
              Arguments.of(artist, HttpStatus.BAD_REQUEST)
      );
    }

    @ParameterizedTest(name = "[{index}] => Artist Id <{0}>")
    @MethodSource("invalidArtistIdProvider")
    @DisplayName("Searching by id with id less than or equal to 0 should return an empty optional")
    void search_by_name_with_invalid_artist_id(long artistId) {
      // when
      Optional<Artist> result = artistSearchClient.searchById(artistId);

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
