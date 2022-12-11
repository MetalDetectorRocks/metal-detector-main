package rocks.metaldetector.persistence.domain.user;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcOperations;
import rocks.metaldetector.persistence.BaseDataJpaTest;
import rocks.metaldetector.persistence.WithIntegrationTestConfig;

import java.util.Optional;

class UserRepositoryIT extends BaseDataJpaTest implements WithAssertions, WithIntegrationTestConfig {

  private static final String UNKNOWN_USERNAME = "Unknown";
  private static final String UNKNOWN_EMAIL = "unknown@example.com";

  @Autowired
  private UserRepository underTest;

  @Autowired
  private JdbcOperations jdbcOperations;

  private UserEntity johnDoe = UserFactory.createUser("JohnD", "john.doe@example.com");
  private UserEntity janeDoe = UserFactory.createUser("JaneD", "jane.doe@example.com");
  private OAuthUserEntity oAuthUser = OAuthUserFactory.createUser("OAuthUsername", "oauth@example.com");

  @BeforeEach
  void setup() {
    johnDoe = underTest.save(johnDoe);
    janeDoe = underTest.save(janeDoe);
    oAuthUser = underTest.save(oAuthUser);
  }

  @AfterEach
  void tearDown() {
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
    Optional<AbstractUserEntity> user = underTest.findByEmail(johnDoe.getEmail());

    assertThat(user).isPresent();
    assertThat(user.get()).isEqualTo(johnDoe);
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
    Optional<AbstractUserEntity> user = underTest.findByUsername(johnDoe.getUsername());

    assertThat(user).isPresent();
    assertThat(user.get()).isEqualTo(johnDoe);
  }

  @Test
  @DisplayName("findByUsername() should not return oauth user entity")
  void find_by_username_should_not_return_oauth_user_entity() {
    Optional<AbstractUserEntity> user = underTest.findByUsername(oAuthUser.getUsername());

    assertThat(user).isEmpty();
  }

  @Test
  @DisplayName("findByUsername() should return an empty optional if no user was found")
  void find_by_username_should_return_empty_optional() {
    Optional<AbstractUserEntity> user = underTest.findByUsername(UNKNOWN_USERNAME);

    assertThat(user).isEmpty();
  }

  @Test
  @DisplayName("getByUsername() should return the correct user entity")
  void get_by_username_should_return_the_correct_user_entity() {
    // when
    UserEntity user = underTest.getByUsername(johnDoe.getUsername());

    // then
    assertThat(user).isEqualTo(johnDoe);
  }

  @Test
  @DisplayName("getByUsername() should not return oauth user entity")
  void get_by_username_should_not_return_oauth_user_entity() {
    // given
    UserEntity user = underTest.getByUsername(oAuthUser.getUsername());

    // then
    assertThat(user).isNull();
  }

  @Test
  @DisplayName("findByPublicId() should return the correct user entity")
  void find_by_public_id_should_return_user_entity() {
    Optional<AbstractUserEntity> user = underTest.findByPublicId(johnDoe.getPublicId());

    assertThat(user).isPresent();
    assertThat(user.get()).isEqualTo(johnDoe);
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
    assertThat(underTest.existsByEmail(johnDoe.getEmail())).isTrue();
    assertThat(underTest.existsByEmail(UNKNOWN_EMAIL)).isFalse();
  }

  @Test
  @DisplayName("existsByUsername() should return true for existing users and false for not existing users")
  void exists_by_username_should_return_true_or_false() {
    assertThat(underTest.existsByUsername(johnDoe.getUsername())).isTrue();
    assertThat(underTest.existsByUsername(UNKNOWN_USERNAME)).isFalse();
  }

  @Test
  @DisplayName("existsByUsername() should return false for oauth users")
  void exists_by_username_should_return_false_for_oauth_users() {
    assertThat(underTest.existsByUsername(oAuthUser.getUsername())).isFalse();
  }

  @Test
  @DisplayName("findAllExpiredUsers() returns correct user")
  void test_find_expired_returns_users() {
    // given
    var expiredUser = UserFactory.createUser("Expired", "expired@example.com");
    expiredUser.setEnabled(false);
    underTest.save(expiredUser);
    jdbcOperations.execute(
        "update users set created_date = CURRENT_DATE - INTERVAL '12' DAY, " +
        "last_modified_date = CURRENT_DATE - INTERVAL '12' DAY " +
        "where username = 'Expired';"
    );

    // when
    var result = underTest.findAllExpiredUsers();

    // then
    assertThat(result).containsExactly(expiredUser);
  }
}
