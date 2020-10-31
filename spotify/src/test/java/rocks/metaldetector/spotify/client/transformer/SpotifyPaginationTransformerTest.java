package rocks.metaldetector.spotify.client.transformer;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import rocks.metaldetector.spotify.api.SpotifyPaginatedResult;
import rocks.metaldetector.support.Pagination;

import java.util.stream.Stream;

class SpotifyPaginationTransformerTest implements WithAssertions {

  SpotifyPaginationTransformer underTest = new SpotifyPaginationTransformer();

  @ParameterizedTest
  @MethodSource("paginationProvider")
  @DisplayName("paginatedResult is transformed to Pagination")
  void test_paginated_result_is_transformed(int total, int offset, int limit, Pagination expectedPagination) {
    // given
    var paginatedResult = new SpotifyPaginatedResult();
    paginatedResult.setTotal(total);
    paginatedResult.setOffset(offset);
    paginatedResult.setLimit(limit);

    // when
    var result = underTest.transform(paginatedResult);

    // then
    assertThat(result).isEqualTo(expectedPagination);
  }

  private static Stream<Arguments> paginationProvider() {
    return Stream.of(
        Arguments.of(1, 0, 10, Pagination.builder().currentPage(1).itemsPerPage(10).totalPages(1).build()),
        Arguments.of(15, 10, 10, Pagination.builder().currentPage(2).itemsPerPage(10).totalPages(2).build()),
        Arguments.of(30, 5, 20, Pagination.builder().currentPage(1).itemsPerPage(20).totalPages(2).build()),
        Arguments.of(4000, 1960, 40, Pagination.builder().currentPage(50).itemsPerPage(40).totalPages(50).build())
    );
  }
}