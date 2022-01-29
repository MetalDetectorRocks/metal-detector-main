package rocks.metaldetector.persistence.domain.token;

import org.assertj.core.api.WithAssertions;
import org.assertj.core.data.TemporalUnitLessThanOffset;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.time.Duration;
import java.time.LocalDateTime;

import static java.time.temporal.ChronoUnit.HOURS;
import static java.time.temporal.ChronoUnit.SECONDS;
import static org.mockito.Mockito.mockStatic;
import static rocks.metaldetector.persistence.domain.token.TokenFactory.DUMMY_TOKEN_STRING;
import static rocks.metaldetector.persistence.domain.token.TokenType.EMAIL_VERIFICATION;

class TokenEntityTest implements WithAssertions {

  @Test
  @DisplayName("All fields of a TokenEntity should have valid values after creation")
  void all_token_fields_should_have_valid_values() {
    // given
    LocalDateTime expectedExpirationDateTime = LocalDateTime.now().plus(1, HOURS);
    TemporalUnitLessThanOffset offset = new TemporalUnitLessThanOffset(1, SECONDS);

    // when
    TokenEntity underTest = TokenFactory.createToken(EMAIL_VERIFICATION, Duration.ofHours(1).toMillis());

    // then
    assertThat(underTest.getExpirationDateTime()).isCloseTo(expectedExpirationDateTime, offset);
    assertThat(underTest.isExpired()).isFalse();
    assertThat(underTest.getUser()).isNotNull();
    assertThat(underTest.getTokenString()).hasSize(DUMMY_TOKEN_STRING.length());
    assertThat(underTest.getTokenType()).isEqualTo(EMAIL_VERIFICATION);
  }

  @Test
  @DisplayName("The token should be expired after the set expiration time has passed")
  void token_should_expire_after_expiration_time() {
    // given
    var now = LocalDateTime.now().plusHours(1);

    // when
    TokenEntity underTest = TokenFactory.createToken(50);

    // then
    assertThat(underTest.isExpired()).isFalse();
    try (MockedStatic<LocalDateTime> mock = mockStatic(LocalDateTime.class)) {
      mock.when(LocalDateTime::now).thenReturn(now);
      assertThat(underTest.isExpired()).isTrue();
    }
  }
}
