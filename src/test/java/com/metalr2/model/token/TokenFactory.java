package com.metalr2.model.token;

import com.metalr2.model.user.UserEntity;
import com.metalr2.model.user.UserFactory;
import com.metalr2.security.ExpirationTime;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class TokenFactory {

          static final String DUMMY_TOKEN_STRING = "dummy-token-string";
  private static final UserEntity userEntity     = UserFactory.createUser("JohnD", "john.d@example.com");

  public static TokenEntity createToken() {
    return createToken(ExpirationTime.ONE_HOUR.toMillis());
  }

  public static TokenEntity createToken(TokenType tokenType, long expireInMillis) {
    return createToken(tokenType, userEntity, expireInMillis);
  }

  static TokenEntity createToken(long expireInMillis) {
    return createToken(TokenType.EMAIL_VERIFICATION, userEntity, expireInMillis);
  }

  public static TokenEntity createToken(TokenType tokenType, UserEntity user) {
    return createToken(tokenType, user, ExpirationTime.ONE_HOUR.toMillis());
  }

  private static TokenEntity createToken(TokenType tokenType, UserEntity user, long expireInMillis) {
    return TokenEntity.builder()
            .tokenString(DUMMY_TOKEN_STRING)
            .user(user)
            .tokenType(tokenType)
            .expirationDateTime(LocalDateTime.now().plus(expireInMillis, ChronoUnit.MILLIS))
            .build();
  }

}
