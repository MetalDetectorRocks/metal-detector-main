package rocks.metaldetector.support;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import rocks.metaldetector.support.DetectorSort.Direction;

import java.util.stream.Stream;

import static rocks.metaldetector.support.DetectorSort.Direction.ASC;
import static rocks.metaldetector.support.DetectorSort.Direction.DESC;

class DetectorSortTest implements WithAssertions {

  @ParameterizedTest
  @MethodSource("directionProvider")
  @DisplayName("should create a valid DetectorSort instance")
  void should_create_a_valid_detector_sort_instance(String givenDirection, Direction expected) {
    // when
    var detectorSort = new DetectorSort("field", givenDirection);

    // then
    assertThat(detectorSort.getField()).isEqualTo("field");
    assertThat(detectorSort.getDirection()).isEqualTo(expected);
  }

  private static Stream<Arguments> directionProvider() {
    return Stream.of(
            Arguments.of("asc", ASC),
            Arguments.of("desc", DESC)
    );
  }

  @ParameterizedTest(name = "should use ASC if direction is <{0}>")
  @MethodSource("emptyDirectionProvider")
  @DisplayName("should use ASC as default direction")
  void should_use_asc_as_default_direction(String givenDirection) {
    // when
    var detectorSort = new DetectorSort("field", givenDirection);

    // then
    assertThat(detectorSort.getDirection()).isEqualTo(ASC);
  }

  private static Stream<Arguments> emptyDirectionProvider() {
    return Stream.of(
            Arguments.of(""),
            Arguments.of(" "),
            Arguments.of((String) null)
    );
  }

  @ParameterizedTest(name = "should throw exception if field is <{0}> and direction is <{1}>")
  @MethodSource("fieldProvider")
  @DisplayName("should throw exception if field is invalid")
  void should_throw_exception_if_field_is_invalid(String givenField, String givenDirection) {
    // when
    Throwable throwable = catchThrowable(() -> new DetectorSort(givenField, givenDirection));

    // then
    assertThat(throwable).isInstanceOf(IllegalArgumentException.class);
  }

  private static Stream<Arguments> fieldProvider() {
    return Stream.of(
            Arguments.of("", "asc"),
            Arguments.of(" ", "asc"),
            Arguments.of(null, "asc"),
            Arguments.of("validField", "foo")
    );
  }
}
