package rocks.metaldetector.discogs.facade;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rocks.metaldetector.discogs.client.DiscogsArtistSearchRestClient;
import rocks.metaldetector.discogs.client.DiscogsDtoFactory.DiscogsArtistSearchResultContainerFactory;
import rocks.metaldetector.discogs.client.transformer.DiscogsArtistSearchResultContainerTransformer;
import rocks.metaldetector.discogs.client.transformer.DiscogsArtistTransformer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static rocks.metaldetector.discogs.client.DiscogsDtoFactory.*;
import static rocks.metaldetector.discogs.client.DiscogsDtoFactory.DiscogsArtistSearchResultDtoFactory;

@ExtendWith(MockitoExtension.class)
class DiscogsServiceImplTest implements WithAssertions {

  @Mock
  private DiscogsArtistSearchRestClient searchClient;

  @Mock
  private DiscogsArtistTransformer artistTransformer;

  @Mock
  private DiscogsArtistSearchResultContainerTransformer searchResultTransformer;

  @InjectMocks
  private DiscogsServiceImpl underTest;

  @AfterEach
  void setup() {
    reset(searchClient, artistTransformer, searchResultTransformer);
  }

  @Nested
  @DisplayName("Tests for method searchArtistByName()")
  class SearchByNameTest {

    @Test
    @DisplayName("Should pass provided arguments to search client")
    void should_pass_arguments() {
      // given
      var query = "the query";
      var page = 1;
      var size = 10;

      // when
      underTest.searchArtistByName(query, page, size);

      // then
      verify(searchClient, times(1)).searchByName(query, page, size);
    }

    @Test
    @DisplayName("Should transform the result from search client with search result transformer")
    void should_transform_search_results() {
      // given
      var searchResult = DiscogsArtistSearchResultContainerFactory.createDefault();
      doReturn(searchResult).when(searchClient).searchByName(any(), anyInt(), anyInt());

      // when
      underTest.searchArtistByName("query", 1, 1);

      // then
      verify(searchResultTransformer, times(1)).transform(eq(searchResult));
    }

    @Test
    @DisplayName("Should return the result from search result transformer")
    void should_return_transformed_results() {
      // given
      var searchResult = DiscogsArtistSearchResultContainerFactory.createDefault();
      var transformedSearchResult = DiscogsArtistSearchResultDtoFactory.createDefault();
      doReturn(searchResult).when(searchClient).searchByName(any(), anyInt(), anyInt());
      doReturn(transformedSearchResult).when(searchResultTransformer).transform(any());

      // when
      var response = underTest.searchArtistByName("query", 1, 1);

      // then
      assertThat(response).isEqualTo(transformedSearchResult);
    }
  }

  @Nested
  @DisplayName("Tests for method searchArtistById()")
  class SearchByIdTest {

    @Test
    @DisplayName("Should pass provided artist id to search client")
    void should_pass_arguments() {
      // given
      var artistId = 123;

      // when
      underTest.searchArtistById(artistId);

      // then
      verify(searchClient, times(1)).searchById(artistId);
    }

    @Test
    @DisplayName("Should transform the result from search client with artist transformer")
    void should_transform_search_results() {
      // given
      var searchResult = DiscogsArtistFactory.createDefault();
      doReturn(searchResult).when(searchClient).searchById(anyLong());

      // when
      underTest.searchArtistById(123);

      // then
      verify(artistTransformer, times(1)).transform(eq(searchResult));
    }

    @Test
    @DisplayName("Should return the result from artist transformer")
    void should_return_transformed_results() {
      // given
      var searchResult = DiscogsArtistFactory.createDefault();
      var transformedSearchResult = DiscogsArtistDtoFactory.createDefault();
      doReturn(searchResult).when(searchClient).searchById(anyLong());
      doReturn(transformedSearchResult).when(artistTransformer).transform(any());

      // when
      var response = underTest.searchArtistById(123);

      // then
      assertThat(response).isEqualTo(transformedSearchResult);
    }
  }
}
