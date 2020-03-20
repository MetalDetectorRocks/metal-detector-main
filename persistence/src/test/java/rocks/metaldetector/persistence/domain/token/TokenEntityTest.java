package rocks.metaldetector.persistence.domain.token;

import org.assertj.core.api.WithAssertions;
import org.assertj.core.data.TemporalUnitLessThanOffset;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static rocks.metaldetector.persistence.domain.token.TokenFactory.DUMMY_TOKEN_STRING;

class TokenEntityTest implements WithAssertions {

  @Test
  @DisplayName("All fields of a TokenEntity should have valid values after creation")
  void all_token_fields_should_have_valid_values() {
    TokenEntity token = TokenFactory.createToken(TokenType.EMAIL_VERIFICATION, Duration.ofHours(1).toMillis());

    LocalDateTime expectedExpirationDateTime = LocalDateTime.now().plus(1, ChronoUnit.HOURS);
    TemporalUnitLessThanOffset offset = new TemporalUnitLessThanOffset(1, ChronoUnit.SECONDS);
    assertThat(token.getExpirationDateTime()).isCloseTo(expectedExpirationDateTime, offset);
    assertThat(token.isExpired()).isFalse();
    assertThat(token.getUser()).isNotNull();
    assertThat(token.getTokenString()).hasSize(DUMMY_TOKEN_STRING.length());
    assertThat(token.getTokenType()).isEqualTo(TokenType.EMAIL_VERIFICATION);
  }

  @Test
  @DisplayName("The token should be expired after the set expiration time has passed")
  void token_should_expire_after_expiration_time() throws Exception {
    long expirationTimeInMillis = 50;
    TokenEntity token = TokenFactory.createToken(expirationTimeInMillis);

    assertThat(token.isExpired()).isFalse();
    Thread.sleep(expirationTimeInMillis + 1);
    assertThat(token.isExpired()).isTrue();
  }

}
