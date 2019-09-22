package com.metalr2.model.token;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

import static com.metalr2.model.token.TokenFactory.DUMMY_TOKEN_STRING;

class TokenEntityTest implements WithAssertions {

  @Test
  void allTokenFieldsHaveValidValues() {
    TokenEntity token = TokenFactory.createToken(TokenType.EMAIL_VERIFICATION);

    assertThat(token.getExpirationDateTime());
    assertThat(token.isExpired()).isFalse();
    assertThat(token.getUser()).isNotNull();
    assertThat(token.getTokenString()).hasSize(DUMMY_TOKEN_STRING.length());
    assertThat(token.getTokenType()).isEqualTo(TokenType.EMAIL_VERIFICATION);
  }

  @Test
  void isExpired() throws Exception {
    long expirationTimeInMillis = 50;
    TokenEntity token = TokenFactory.createToken(expirationTimeInMillis);

    assertThat(token.isExpired()).isFalse();
    Thread.sleep(expirationTimeInMillis + 1);
    assertThat(token.isExpired()).isTrue();
  }

}
