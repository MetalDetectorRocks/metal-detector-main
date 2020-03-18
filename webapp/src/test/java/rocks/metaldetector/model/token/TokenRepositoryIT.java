package rocks.metaldetector.model.token;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import rocks.metaldetector.model.user.UserEntity;
import rocks.metaldetector.model.user.UserFactory;
import rocks.metaldetector.model.user.UserRepository;
import rocks.metaldetector.testutil.WithIntegrationTestProfile;

import java.util.Optional;

@DataJpaTest
class TokenRepositoryIT implements WithAssertions, WithIntegrationTestProfile {

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
