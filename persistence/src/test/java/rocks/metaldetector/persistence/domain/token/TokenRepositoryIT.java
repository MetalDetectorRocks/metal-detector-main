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

import java.util.List;
import java.util.Optional;

import static rocks.metaldetector.persistence.domain.token.TokenType.EMAIL_VERIFICATION;
import static rocks.metaldetector.persistence.domain.token.TokenType.PASSWORD_RESET;

class TokenRepositoryIT extends BaseDataJpaTest implements WithAssertions, WithIntegrationTestConfig {

  @Autowired
  private TokenRepository underTest;

  @Autowired
  private UserRepository userRepository;

  private final UserEntity user1 = UserFactory.createUser("user1", "user1@example.com");

  @BeforeEach
  void setup() {
    userRepository.save(user1);
  }

  @AfterEach
  void tearDown() {
    underTest.deleteAll();
    userRepository.deleteAll();
  }

  @Test
  @DisplayName("findEmailVerificationToken() should return correct token entity")
  void find_email_verification_token_should_return_correct_token_entity() {
    TokenEntity emailVerificationToken = createToken(EMAIL_VERIFICATION, user1);

    Optional<TokenEntity> optionalTokenEntity = underTest.findEmailVerificationToken(emailVerificationToken.getTokenString());

    assertThat(optionalTokenEntity).isPresent();
    assertThat(emailVerificationToken).isEqualTo(optionalTokenEntity.get());
  }

  @Test
  @DisplayName("findResetPasswordToken() should return correct token entity")
  void find_reset_password_token_should_return_correct_token_entity() {
    TokenEntity resetPasswordToken = createToken(PASSWORD_RESET, user1);

    Optional<TokenEntity> optionalTokenEntity = underTest.findResetPasswordToken(resetPasswordToken.getTokenString());

    assertThat(optionalTokenEntity).isPresent();
    assertThat(resetPasswordToken).isEqualTo(optionalTokenEntity.get());
  }

  @Test
  @DisplayName("deleteAllByUserIn() returns correct tokens")
  void test_delete_by_user() {
    // given
    UserEntity user2 = UserFactory.createUser("user2", "user2@example.com");
    UserEntity user3 = UserFactory.createUser("user3", "user3@example.com");
    userRepository.save(user2);
    userRepository.save(user3);
    createToken(EMAIL_VERIFICATION, user1);
    createToken(EMAIL_VERIFICATION, user2);
    TokenEntity remainingToken = createToken(EMAIL_VERIFICATION, user3);

    // when
    underTest.deleteAllByUserIn(List.of(user1, user2));
    var tokens = underTest.findAll();

    // then
    assertThat(tokens).containsExactly(remainingToken);
  }

  private TokenEntity createToken(TokenType tokenType, UserEntity userEntity) {
    TokenEntity token = TokenFactory.createToken(tokenType, userEntity);
    underTest.save(token);

    return token;
  }
}
