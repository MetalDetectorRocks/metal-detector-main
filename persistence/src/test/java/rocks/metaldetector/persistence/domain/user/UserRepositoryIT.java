package rocks.metaldetector.persistence.domain.user;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import rocks.metaldetector.persistence.BaseDataJpaTest;
import rocks.metaldetector.persistence.WithIntegrationTestConfig;
import rocks.metaldetector.persistence.domain.token.TokenEntity;
import rocks.metaldetector.persistence.domain.token.TokenRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static java.time.temporal.ChronoUnit.MINUTES;
import static rocks.metaldetector.persistence.domain.token.TokenType.EMAIL_VERIFICATION;

class UserRepositoryIT extends BaseDataJpaTest implements WithAssertions, WithIntegrationTestConfig {

  @Autowired
  private UserRepository underTest;

  @Autowired
  private TokenRepository tokenRepository;

  private static final String USERNAME = "JohnD";
  private static final String OAUTH_USERNAME = "OAuthUsername";
  private static final String EMAIL = "john.doe@example.com";
  private static final String OAUTH_EMAIL = "oauth@example.com";
  private static UserEntity JOHN_DOE = UserFactory.createUser(USERNAME, EMAIL);
  private static UserEntity JANE_DOE = UserFactory.createUser("JaneD", "jane.doe@example.com");
  private static OAuthUserEntity OAUTH_USER = OAuthUserFactory.createUser(OAUTH_USERNAME, OAUTH_EMAIL);
  private static final String UNKNOWN_USERNAME = "Unknown";
  private static final String UNKNOWN_EMAIL = "unknown@example.com";

  @BeforeEach
  void setup() {
    JOHN_DOE = underTest.save(JOHN_DOE);
    JANE_DOE = underTest.save(JANE_DOE);
    OAUTH_USER = underTest.save(OAUTH_USER);

    TokenEntity token1 = TokenEntity.builder().user(JOHN_DOE)
        .tokenString("tokenString")
        .expirationDateTime(LocalDateTime.now().plus(1, MINUTES))
        .tokenType(EMAIL_VERIFICATION)
        .build();
    TokenEntity token2 = TokenEntity.builder().user(JANE_DOE)
        .tokenString("tokenString2")
        .expirationDateTime(LocalDateTime.now().minus(1, MINUTES))
        .tokenType(EMAIL_VERIFICATION)
        .build();
    tokenRepository.save(token1);
    tokenRepository.save(token2);
  }

  @AfterEach
  void tearDown() {
    tokenRepository.deleteAll();
    underTest.deleteAll();
  }

  @Test
  @DisplayName("getPublicId() should return a valid id after persisting the user")
  void get_public_id_should_return_id_after_persisting() {
    UserEntity user = UserFactory.createUser("Test", "test@test.com");

    assertThat(user.getPublicId()).isNull();
    underTest.save(user);
    assertThat(user.getPublicId()).isNotNull();
  }

  @Test
  @DisplayName("getId() should return a valid id after persisting the user")
  void get_id_should_return_id_after_persisting() {
    UserEntity user = UserFactory.createUser("Test", "test@test.com");

    assertThat(user.getId()).isNull();
    assertThat(user.isNew()).isTrue();
    underTest.save(user);
    assertThat(user.getId()).isNotNull();
    assertThat(user.isNew()).isFalse();
  }

  @Test
  @DisplayName("findByEmail() should return the correct user entity")
  void find_by_email_should_return_user_entity() {
    Optional<AbstractUserEntity> user = underTest.findByEmail(EMAIL);

    assertThat(user).isPresent();
    assertThat(user.get()).isEqualTo(JOHN_DOE);
  }

  @Test
  @DisplayName("findByEmail() should return an empty optional if no user was found")
  void find_by_email_should_return_empty_optional() {
    Optional<AbstractUserEntity> user = underTest.findByEmail(UNKNOWN_EMAIL);

    assertThat(user).isEmpty();
  }

  @Test
  @DisplayName("findByUsername() should return the correct user entity")
  void find_by_username_should_return_user_entity() {
    Optional<AbstractUserEntity> user = underTest.findByUsername(USERNAME);

    assertThat(user).isPresent();
    assertThat(user.get()).isEqualTo(JOHN_DOE);
  }

  @Test
  @DisplayName("findByUsername() should not return oauth user entity")
  void find_by_username_should_not_return_oauth_user_entity() {
    Optional<AbstractUserEntity> user = underTest.findByUsername(OAUTH_USERNAME);

    assertThat(user).isEmpty();
  }

  @Test
  @DisplayName("findByUsername() should return an empty optional if no user was found")
  void find_by_username_should_return_empty_optional() {
    Optional<AbstractUserEntity> user = underTest.findByUsername(UNKNOWN_USERNAME);

    assertThat(user).isEmpty();
  }

  @Test
  @DisplayName("findByPublicId() should return the correct user entity")
  void find_by_public_id_should_return_user_entity() {
    Optional<AbstractUserEntity> user = underTest.findByPublicId(JOHN_DOE.getPublicId());

    assertThat(user).isPresent();
    assertThat(user.get()).isEqualTo(JOHN_DOE);
  }

  @Test
  @DisplayName("findByPublicId() should return an empty optional if no user was found")
  void find_by_public_id_should_return_empty_optional() {
    Optional<AbstractUserEntity> user = underTest.findByPublicId(UNKNOWN_USERNAME);

    assertThat(user).isEmpty();
  }

  @Test
  @DisplayName("existsByEmail() should return true for existing users and false for not existing users")
  void exists_by_email_should_return_true_or_false() {
    assertThat(underTest.existsByEmail(EMAIL)).isTrue();
    assertThat(underTest.existsByEmail(UNKNOWN_EMAIL)).isFalse();
  }

  @Test
  @DisplayName("existsByUsername() should return true for existing users and false for not existing users")
  void exists_by_username_should_return_true_or_false() {
    assertThat(underTest.existsByUsername(USERNAME)).isTrue();
    assertThat(underTest.existsByUsername(UNKNOWN_USERNAME)).isFalse();
  }

  @Test
  @DisplayName("existsByUsername() should return false for oauth users")
  void exists_by_username_should_return_false_for_oauth_users() {
    assertThat(underTest.existsByUsername(OAUTH_USERNAME)).isFalse();
  }

  @Test
  @DisplayName("findAllWithExpiredToken() returns correct user")
  void test_find_expired_returns_users() {
    // when
    var result = underTest.findAllWithExpiredToken();

    // then
    assertThat(result).containsExactly(JANE_DOE);
  }
}
