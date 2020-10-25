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
        Arguments.of(10, 5, 1, Pagination.builder().currentPage(6).itemsPerPage(1).totalPages(10).build()),
        Arguments.of(10, 0, 1, Pagination.builder().currentPage(1).itemsPerPage(1).totalPages(10).build()),
        Arguments.of(10, 9, 2, Pagination.builder().currentPage(5).itemsPerPage(2).totalPages(5).build())
    );
  }
}