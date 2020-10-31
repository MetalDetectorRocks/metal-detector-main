package rocks.metaldetector.support;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

class SlicingServiceTest implements WithAssertions {

  private final SlicingService underTest = new SlicingService();

  @ParameterizedTest
  @MethodSource("valueProvider")
  @DisplayName("Should slice the correct part of the list")
  void test(List<Integer> source, int page, int size, List<Integer> expected) {
    // when
    List<Integer> result = underTest.slice(source, page, size);

    // then
    assertThat(result).isEqualTo(expected);
  }

  private static Stream<Arguments> valueProvider() {
    return Stream.of(
            Arguments.of(createIntegerList(20), 1, 5, List.of(1, 2, 3, 4, 5)),
            Arguments.of(createIntegerList(20), 2, 5, List.of(6, 7, 8, 9, 10)),
            Arguments.of(createIntegerList(20), 3, 5, List.of(11, 12, 13, 14, 15)),
            Arguments.of(createIntegerList(20), 4, 5, List.of(16, 17, 18, 19, 20)),
            Arguments.of(createIntegerList(16), 4, 5, List.of(16)),
            Arguments.of(createIntegerList(10), 0, 5, List.of(1, 2, 3, 4, 5)),
            Arguments.of(createIntegerList(10), -1, 5, List.of(1, 2, 3, 4, 5))
    );
  }

  private static List<Integer> createIntegerList(int size) {
    return IntStream.range(1, size + 1).boxed().collect(Collectors.toList());
  }
}