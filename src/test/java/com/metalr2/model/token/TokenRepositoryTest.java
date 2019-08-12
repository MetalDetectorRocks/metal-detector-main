package com.metalr2.model.token;

import com.metalr2.security.ExpirationTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class TokenRepositoryTest {

  @Autowired
  private TokenRepository tokenRepository;

  @Autowired
  private JwtsSupport jwtsSupport;

  private TokenEntity emailVerificationToken;
  private TokenEntity resetPasswordToken;

  @BeforeEach
  void setUp() {
    // ToDo DanielW: do test...
    // String tokenString = jwtsSupport.generateToken(userId, ExpirationTime.ONE_HOUR);

    TokenEntity emailVerificationToken = new TokenEntity();
//    emailVerificationToken.setUser(userEntity);
//    emailVerificationToken.setTokenString(tokenString);
    emailVerificationToken.setExpirationDateTime(LocalDateTime.now().plus(ExpirationTime.ONE_HOUR.toMillis(), ChronoUnit.MILLIS));
    emailVerificationToken.setTokenType(TokenType.EMAIL_VERIFICATION);

    TokenEntity resetPasswordToken = new TokenEntity();
//    resetPasswordToken.setUser(userEntity);
//    resetPasswordToken.setTokenString(tokenString);
    resetPasswordToken.setExpirationDateTime(LocalDateTime.now().plus(ExpirationTime.ONE_HOUR.toMillis(), ChronoUnit.MILLIS));
    resetPasswordToken.setTokenType(TokenType.PASSWORD_RESET);
  }

  @Test
  void findEmailVerificationTokenShouldReturnTokenEntity() {
  }

  @Test
  void findResetPasswordToken() {
  }

}
