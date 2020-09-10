package rocks.metaldetector.web.api.request;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.util.Collections;
import java.util.stream.Stream;

class WithTimeRangeValidationTest implements WithAssertions {

  @ParameterizedTest(name = "<{0}> should return <{1}> when calling isValid()")
  @MethodSource("releaseRequestProvider")
  @DisplayName("If dateTo and dateFrom are set dateFrom must be equal or before dateTo")
  void test_is_valid(WithTimeRangeValidation releasesRequest, boolean expectedResult) {
    // when
    boolean result = releasesRequest.isValid();

    // then
    assertThat(result).isEqualTo(expectedResult);
  }

  private static Stream<Arguments> releaseRequestProvider() {
    var now = LocalDate.now();
    return Stream.of(
            Arguments.of(new ReleasesRequest(null, null), true),
            Arguments.of(new ReleasesRequest(now, null), true),
            Arguments.of(new ReleasesRequest(null, now), true),
            Arguments.of(new ReleasesRequest(now, now), true),
            Arguments.of(new ReleasesRequest(now, now.plusDays(1)), true),
            Arguments.of(new ReleasesRequest(now.plusDays(1), now), false)
    );
  }
}
