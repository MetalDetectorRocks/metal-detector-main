package rocks.metaldetector.security;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static rocks.metaldetector.security.XSSUtils.stripXSS;

class XSSUtilsTest implements WithAssertions {

  @Test
  @DisplayName("stripXSS is nullsafe")
  void test_null_safety() {
    // when
    var result = stripXSS(null);

    // then
    assertThat(result).isNull();
  }

  @Test
  @DisplayName("HTML/JS is removed from given String")
  void test_js_removed() {
    // given
    var badInput = "<h1>Darkthrone</h1>";
    var sanitizedInput = "Darkthrone";

    // when
    var result = XSSUtils.stripXSS(badInput);

    // then
    assertThat(result).isEqualTo(sanitizedInput);
  }
}
