package rocks.metaldetector.web.api.response;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ArtistSearchResponseTest implements WithAssertions {

  @Test
  @DisplayName("empty() returns resultContainer with empty list of items")
  void test_empty_items() {
    // when
    var result = ArtistSearchResponse.empty();

    // then
    assertThat(result).isNotNull();
    assertThat(result.getSearchResults()).isEmpty();
  }

  @Test
  @DisplayName("empty() returns resultContainer with empty query")
  void test_empty_query() {
    // when
    var result = ArtistSearchResponse.empty();

    // then
    assertThat(result).isNotNull();
    assertThat(result.getQuery()).isBlank();
  }

  @Test
  @DisplayName("empty() returns resultContainer with currentPage 1 and everything else 0")
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
