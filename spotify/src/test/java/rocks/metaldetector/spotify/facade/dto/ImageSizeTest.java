package rocks.metaldetector.support;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import rocks.metaldetector.support.ImageSize;

import java.util.stream.Stream;

import static rocks.metaldetector.support.ImageSize.L;
import static rocks.metaldetector.support.ImageSize.M;
import static rocks.metaldetector.support.ImageSize.S;
import static rocks.metaldetector.support.ImageSize.XS;

class ImageSizeTest implements WithAssertions {

  @ParameterizedTest(name = "should convert {0} to image size {1}")
  @MethodSource("heightProvider")
  @DisplayName("should convert height")
  void should_convert_height(int givenHeight, ImageSize expectedSize) {
    // when
    ImageSize size = ImageSize.ofHeight(givenHeight);

    // then
    assertThat(size).isEqualTo(expectedSize);
  }

  private static Stream<Arguments> heightProvider() {
    return Stream.of(
            Arguments.of(0, XS),
            Arguments.of(50, XS),
            Arguments.of(160, S),
            Arguments.of(320, M),
            Arguments.of(650, L)
    );
  }
}
