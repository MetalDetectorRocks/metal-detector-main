package rocks.metaldetector.butler.client.transformer;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import rocks.metaldetector.support.DetectorSort;

import java.util.stream.Stream;

class ButlerSortTransformerTest implements WithAssertions {

  private final ButlerSortTransformer underTest = new ButlerSortTransformer();

  @ParameterizedTest(name = "should transform {0} and {1} to {2}")
  @MethodSource("fieldProvider")
  @DisplayName("should transform DetectorSort")
  void should_transform_detector_sort(String givenField, String givenDirection, String expected) {
    // given
    var detectorSort = new DetectorSort(givenField, givenDirection);

    // when
    var result = underTest.transform(detectorSort);

    // then
    assertThat(result).isEqualTo(expected);
  }

  private static Stream<Arguments> fieldProvider() {
    return Stream.of(
            Arguments.of("release_date", "asc", "sort=releaseDate,ASC&sort=artist,ASC&sort=albumTitle,ASC"),
            Arguments.of("release_date", "desc", "sort=releaseDate,DESC&sort=artist,ASC&sort=albumTitle,ASC"),
            Arguments.of("announcement_date", "asc", "sort=createdDateTime,ASC&sort=artist,ASC&sort=albumTitle,ASC"),
            Arguments.of("announcement_date", "desc", "sort=createdDateTime,DESC&sort=artist,ASC&sort=albumTitle,ASC"),
            Arguments.of("foo", "asc", "sort=foo,ASC&sort=artist,ASC&sort=albumTitle,ASC"),
            Arguments.of("foo", "desc", "sort=foo,DESC&sort=artist,ASC&sort=albumTitle,ASC")
    );
  }
}
