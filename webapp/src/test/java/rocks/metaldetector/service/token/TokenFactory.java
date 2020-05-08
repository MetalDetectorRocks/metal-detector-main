package rocks.metaldetector.service.token;

import rocks.metaldetector.persistence.domain.token.TokenEntity;
import rocks.metaldetector.persistence.domain.token.TokenType;
import rocks.metaldetector.persistence.domain.user.UserEntity;
import rocks.metaldetector.service.user.UserEntityFactory;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class TokenFactory {

  private static final String DUMMY_TOKEN_STRING = "dummy-token-string";
  private static final UserEntity USER_ENTITY    = UserEntityFactory.createUser("JohnD", "john.d@example.com");

  static TokenEntity createDefault() {
    return createToken(TokenType.EMAIL_VERIFICATION, USER_ENTITY, Duration.ofHours(1).toMillis());
  }

  public static TokenEntity createToken(TokenType tokenType, long expireInMillis) {
    return createToken(tokenType, USER_ENTITY, expireInMillis);
  }

  public static TokenEntity createToken(TokenType tokenType, UserEntity user) {
    return createToken(tokenType, user, Duration.ofHours(1).toMillis());
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
