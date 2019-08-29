package com.metalr2.model.token;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TokenEntityTest {

  @Test
  void isExpired() throws Exception {
    long expirationTimeInMillis = 50;
    TokenEntity token = TokenFactory.createToken(expirationTimeInMillis);

    assertFalse(token.isExpired());
    Thread.sleep(expirationTimeInMillis + 1);
    assertTrue(token.isExpired());
  }

}