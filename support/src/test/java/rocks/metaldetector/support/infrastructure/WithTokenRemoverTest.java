package rocks.metaldetector.support.infrastructure;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class WithTokenRemoverTest implements WithAssertions {

  private WithTokenRemover underTest = new WithTokenRemover() {};

  @Test
  @DisplayName("Method is null safe")
  void test_is_null_safe() {
    // when
    String result = underTest.removeTokenForLogging(null);

    // then
    assertThat(result).isNull();
  }

  @Test
  @DisplayName("If input contains authorization header, the value is removed")
  void authorization_header_value_is_removed() {
    // given
    String givenHeaderString = "[... Authorization:\"TOKEN_STRING\" ...]";
    String expectedHeaderString = "[... Authorization:\"REMOVED_FOR_LOGGING\" ...]";

    // when
    String result = underTest.removeTokenForLogging(givenHeaderString);

    // then
    assertThat(result).isEqualTo(expectedHeaderString);
  }

  @Test
  @DisplayName("If input does not contain authorization header, the input is returned")
  void input_is_returned() {
    // given
    String givenHeaderString = "... Accept:\"application/json\" ...";

    // when
    String result = underTest.removeTokenForLogging(givenHeaderString);

    // then
    assertThat(result).isEqualTo(givenHeaderString);
  }
}