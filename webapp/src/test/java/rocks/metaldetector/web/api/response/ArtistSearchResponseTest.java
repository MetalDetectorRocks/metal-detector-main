package rocks.metaldetector.web.api.response;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import rocks.metaldetector.support.Pagination;

import java.util.List;

import static org.mockito.Mockito.mock;

class ArtistSearchResponseTest implements WithAssertions {

  @Nested
  class EmptyArtistSearchResponseTests {

    @Test
    @DisplayName("should return resultContainer with empty list of items")
    void test_empty_items() {
      // when
      var result = ArtistSearchResponse.empty();

      // then
      assertThat(result).isNotNull();
      assertThat(result.getSearchResults()).isEmpty();
    }

    @Test
    @DisplayName("should return resultContainer with empty query")
    void test_empty_query() {
      // when
      var result = ArtistSearchResponse.empty();

      // then
      assertThat(result).isNotNull();
      assertThat(result.getQuery()).isBlank();
    }

    @Test
    @DisplayName("should return resultContainer with currentPage 1 and everything else 0")
    void test_empty_pagination() {
      // when
      var result = ArtistSearchResponse.empty();

      // then
      assertThat(result).isNotNull();
      assertThat(result.getPagination()).isNotNull();
      assertThat(result.getPagination().getCurrentPage()).isEqualTo(1);
      assertThat(result.getPagination().getItemsPerPage()).isEqualTo(0);
      assertThat(result.getPagination().getTotalPages()).isEqualTo(0);
    }
  }

  @Nested
  class SearchResultsTitleTests {

    @Test
    @DisplayName("should return title for no results")
    void should_return_title_for_no_results() {
      // given
      ArtistSearchResponse response = new ArtistSearchResponse(
          "Slayer",
          new Pagination(1, 1, 10),
          List.of()
      );

      // when
      String result = response.getSearchResultsTitle();

      // then
      assertThat(result).isEqualTo("No result for \"Slayer\"");
    }

    @Test
    @DisplayName("should return title for only one result")
    void should_return_title_for_only_one_result() {
      // given
      ArtistSearchResponse response = new ArtistSearchResponse(
          "Slayer",
          new Pagination(1, 1, 10),
          List.of(
              mock(ArtistSearchResponseEntryDto.class)
          )
      );

      // when
      String result = response.getSearchResultsTitle();

      // then
      assertThat(result).isEqualTo("1 result for \"Slayer\"");
    }

    @Test
    @DisplayName("should return title for results on only one page")
    void should_return_title_for_results_on_only_one_page() {
      // given
      ArtistSearchResponse response = new ArtistSearchResponse(
          "Slayer",
          new Pagination(1, 1, 10),
          List.of(
              mock(ArtistSearchResponseEntryDto.class),
              mock(ArtistSearchResponseEntryDto.class),
              mock(ArtistSearchResponseEntryDto.class)
          )
      );

      // when
      String result = response.getSearchResultsTitle();

      // then
      assertThat(result).isEqualTo("3 results for \"Slayer\"");
    }

    @Test
    @DisplayName("should return title for results on more than one page")
    void should_return_title_for_results_on_more_than_one_page() {
      // given
      ArtistSearchResponse response = new ArtistSearchResponse(
          "Slayer",
          new Pagination(3, 1, 10),
          List.of(
              mock(ArtistSearchResponseEntryDto.class)
          )
      );

      // when
      String result = response.getSearchResultsTitle();

      // then
      assertThat(result).isEqualTo("More than 20 results for \"Slayer\"");
    }
  }
}
