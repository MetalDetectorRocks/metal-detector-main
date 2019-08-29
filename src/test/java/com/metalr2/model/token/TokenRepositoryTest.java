package com.metalr2.model.token;

import com.metalr2.model.user.UserEntity;
import com.metalr2.model.user.UserFactory;
import com.metalr2.model.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class TokenRepositoryTest {

  @Autowired
  private TokenRepository tokenRepository;

  @Autowired
  private UserRepository userRepository;

  private UserEntity user;

  @BeforeEach
  void setup() {
    user = UserFactory.createUser("user", "user@example.com");
    userRepository.save(user);
  }

  @AfterEach
  void tearDown() {
    tokenRepository.deleteAll();
    userRepository.deleteAll();
  }

  @Test
  void findEmailVerificationTokenShouldReturnTokenEntity() {
    TokenEntity emailVerificationToken = TokenFactory.createToken(TokenType.EMAIL_VERIFICATION, user);
    tokenRepository.save(emailVerificationToken);

    Optional<TokenEntity> optionalTokenEntity = tokenRepository.findEmailVerificationToken(emailVerificationToken.getTokenString());

    assertTrue(optionalTokenEntity.isPresent());
    assertEquals(emailVerificationToken, optionalTokenEntity.get());
  }

  @Test
  void findResetPasswordToken() {
    TokenEntity resetPasswordToken = TokenFactory.createToken(TokenType.PASSWORD_RESET, user);
    tokenRepository.save(resetPasswordToken);

    Optional<TokenEntity> optionalTokenEntity = tokenRepository.findResetPasswordToken(resetPasswordToken.getTokenString());

    assertTrue(optionalTokenEntity.isPresent());
    assertEquals(resetPasswordToken, optionalTokenEntity.get());
  }

}
