package rocks.metaldetector.persistence.domain.token;

import rocks.metaldetector.persistence.domain.user.UserEntity;
import rocks.metaldetector.persistence.domain.user.UserFactory;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class TokenFactory {

  static final String DUMMY_TOKEN_STRING = "dummy-token-string";
  private static final UserEntity userEntity = UserFactory.createUser("JohnD", "john.d@example.com");

  public static TokenEntity createToken(TokenType tokenType, long expireInMillis) {
    return createToken(tokenType, userEntity, expireInMillis);
  }

  public static TokenEntity createToken(long expireInMillis) {
    return createToken(TokenType.EMAIL_VERIFICATION, userEntity, expireInMillis);
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
