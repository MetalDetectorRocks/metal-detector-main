package rocks.metaldetector.persistence.domain.token;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import rocks.metaldetector.persistence.BaseDataJpaTest;
import rocks.metaldetector.persistence.WithIntegrationTestConfig;
import rocks.metaldetector.persistence.domain.user.UserEntity;
import rocks.metaldetector.persistence.domain.user.UserFactory;
import rocks.metaldetector.persistence.domain.user.UserRepository;

import java.util.Optional;

class TokenRepositoryIT extends BaseDataJpaTest implements WithAssertions, WithIntegrationTestConfig {

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
  @DisplayName("findEmailVerificationToken() should return correct token entity")
  void find_email_verification_token_should_return_correct_token_entity() {
    TokenEntity emailVerificationToken = createToken(TokenType.EMAIL_VERIFICATION);

    Optional<TokenEntity> optionalTokenEntity = tokenRepository.findEmailVerificationToken(emailVerificationToken.getTokenString());

    assertThat(optionalTokenEntity).isPresent();
    assertThat(emailVerificationToken).isEqualTo(optionalTokenEntity.get());
  }

  @Test
  @DisplayName("findResetPasswordToken() should return correct token entity")
  void find_reset_password_token_should_return_correct_token_entity() {
    TokenEntity resetPasswordToken = createToken(TokenType.PASSWORD_RESET);

    Optional<TokenEntity> optionalTokenEntity = tokenRepository.findResetPasswordToken(resetPasswordToken.getTokenString());

    assertThat(optionalTokenEntity).isPresent();
    assertThat(resetPasswordToken).isEqualTo(optionalTokenEntity.get());
  }

  private TokenEntity createToken(TokenType tokenType) {
    TokenEntity token = TokenFactory.createToken(tokenType, user);
    tokenRepository.save(token);

    return token;
  }

}
