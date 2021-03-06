package rocks.metaldetector.persistence.domain.user;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import rocks.metaldetector.persistence.BaseDataJpaTest;
import rocks.metaldetector.persistence.WithIntegrationTestConfig;

import java.util.Optional;

class UserRepositoryIT extends BaseDataJpaTest implements WithAssertions, WithIntegrationTestConfig {

  @Autowired
  private UserRepository userRepository;

  private static final String USERNAME = "JohnD";
  private static final String OAUTH_USERNAME = "OAuthUsername";
  private static final String EMAIL = "john.doe@example.com";
  private static final String OAUTH_EMAIL = "oauth@example.com";
  private static UserEntity JOHN_DOE = UserFactory.createUser(USERNAME, EMAIL);
  private static OAuthUserEntity OAUTH_USER = OAuthUserFactory.createUser(OAUTH_USERNAME, OAUTH_EMAIL);
  private static final String UNKNOWN_USERNAME = "Unknown";
  private static final String UNKNOWN_EMAIL = "unknown@example.com";

  @BeforeEach
  void setup() {
    JOHN_DOE = userRepository.save(JOHN_DOE);
    OAUTH_USER = userRepository.save(OAUTH_USER);
  }

  @AfterEach
  void tearDown() {
    userRepository.deleteAll();
  }

  @Test
  @DisplayName("getPublicId() should return a valid id after persisting the user")
  void get_public_id_should_return_id_after_persisting() {
    UserEntity user = UserFactory.createUser("Test", "test@test.com");

    assertThat(user.getPublicId()).isNull();
    userRepository.save(user);
    assertThat(user.getPublicId()).isNotNull();
  }

  @Test
  @DisplayName("getId() should return a valid id after persisting the user")
  void get_id_should_return_id_after_persisting() {
    UserEntity user = UserFactory.createUser("Test", "test@test.com");

    assertThat(user.getId()).isNull();
    assertThat(user.isNew()).isTrue();
    userRepository.save(user);
    assertThat(user.getId()).isNotNull();
    assertThat(user.isNew()).isFalse();
  }

  @Test
  @DisplayName("findByEmail() should return the correct user entity")
  void find_by_email_should_return_user_entity() {
    Optional<AbstractUserEntity> user = userRepository.findByEmail(EMAIL);

    assertThat(user).isPresent();
    assertThat(user.get()).isEqualTo(JOHN_DOE);
  }

  @Test
  @DisplayName("findByEmail() should return an empty optional if no user was found")
  void find_by_email_should_return_empty_optional() {
    Optional<AbstractUserEntity> user = userRepository.findByEmail(UNKNOWN_EMAIL);

    assertThat(user).isEmpty();
  }

  @Test
  @DisplayName("findByUsername() should return the correct user entity")
  void find_by_username_should_return_user_entity() {
    Optional<AbstractUserEntity> user = userRepository.findByUsername(USERNAME);

    assertThat(user).isPresent();
    assertThat(user.get()).isEqualTo(JOHN_DOE);
  }

  @Test
  @DisplayName("findByUsername() should not return oauth user entity")
  void find_by_username_should_not_return_oauth_user_entity() {
    Optional<AbstractUserEntity> user = userRepository.findByUsername(OAUTH_USERNAME);

    assertThat(user).isEmpty();
  }

  @Test
  @DisplayName("findByUsername() should return an empty optional if no user was found")
  void find_by_username_should_return_empty_optional() {
    Optional<AbstractUserEntity> user = userRepository.findByUsername(UNKNOWN_USERNAME);

    assertThat(user).isEmpty();
  }

  @Test
  @DisplayName("findByPublicId() should return the correct user entity")
  void find_by_public_id_should_return_user_entity() {
    Optional<AbstractUserEntity> user = userRepository.findByPublicId(JOHN_DOE.getPublicId());

    assertThat(user).isPresent();
    assertThat(user.get()).isEqualTo(JOHN_DOE);
  }

  @Test
  @DisplayName("findByPublicId() should return an empty optional if no user was found")
  void find_by_public_id_should_return_empty_optional() {
    Optional<AbstractUserEntity> user = userRepository.findByPublicId(UNKNOWN_USERNAME);

    assertThat(user).isEmpty();
  }

  @Test
  @DisplayName("existsByEmail() should return true for existing users and false for not existing users")
  void exists_by_email_should_return_true_or_false() {
    assertThat(userRepository.existsByEmail(EMAIL)).isTrue();
    assertThat(userRepository.existsByEmail(UNKNOWN_EMAIL)).isFalse();
  }

  @Test
  @DisplayName("existsByUsername() should return true for existing users and false for not existing users")
  void exists_by_username_should_return_true_or_false() {
    assertThat(userRepository.existsByUsername(USERNAME)).isTrue();
    assertThat(userRepository.existsByUsername(UNKNOWN_USERNAME)).isFalse();
  }

  @Test
  @DisplayName("existsByUsername() should return false for oauth users")
  void exists_by_username_should_return_false_for_oauth_users() {
    assertThat(userRepository.existsByUsername(OAUTH_USERNAME)).isFalse();
  }
}
