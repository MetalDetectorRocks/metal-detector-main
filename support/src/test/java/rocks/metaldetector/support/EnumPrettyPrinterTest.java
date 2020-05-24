package rocks.metaldetector.support;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

class EnumPrettyPrinterTest implements WithAssertions {

  private final EnumPrettyPrinter underTest = new EnumPrettyPrinter();

  @Test
  @DisplayName("Should be null safe")
  void should_be_null_safe() {
    // when
    String result = underTest.prettyPrintEnumValue(null);

    // then
    assertThat(result).isNull();
  }

  @ParameterizedTest(name = "Should transform <{0}> to <{1}>")
  @MethodSource("valueProvider")
  @DisplayName("Should transform the given value fully capitalized")
  void should_transform_fully_capitalized(String givenValue, String expectedValue) {
    // when
    String result = underTest.prettyPrintEnumValue(givenValue);

    // then
    assertThat(result).isEqualTo(expectedValue);
  }

  private static Stream<Arguments> valueProvider() {
    return Stream.of(
            Arguments.of("value", "Value"),
            Arguments.of("_value_", "Value"),
            Arguments.of("example_value", "Example Value"),
            Arguments.of("EXAMPLE_VALUE", "Example Value"),
            Arguments.of("eXAMPLE_vALUE", "Example Value")
    );
  }
}