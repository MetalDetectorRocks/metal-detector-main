package com.metalr2.model.user;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@Tag("integration-test")
class UserRepositoryIT implements WithAssertions {

  @Autowired
  private UserRepository userRepository;

  private static final String     USERNAME         = "JohnD";
  private static final String     EMAIL            = "john.doe@example.com";
  private static       UserEntity JOHN_DOE         = UserFactory.createUser(USERNAME, EMAIL);
  private static final String     UNKNOWN_USERNAME = "Unknown";
  private static final String     UNKNOWN_EMAIL    = "unknown@example.com";

  @BeforeEach
  void setup() {
    JOHN_DOE = userRepository.save(JOHN_DOE);
  }

  @AfterEach
  void tearDown() {
    userRepository.deleteAll();
  }

  @Test
  void get_public_id_should_return_id_after_persisting() {
    UserEntity user = UserFactory.createUser("Test", "test@test.com");

    assertThat(user.getPublicId()).isNull();
    userRepository.save(user);
    assertThat(user.getPublicId()).isNotNull();
  }

  @Test
  void get_id_should_return_id_after_persisting() {
    UserEntity user = UserFactory.createUser("Test", "test@test.com");

    assertThat(user.getId()).isNull();
    assertThat(user.isNew()).isTrue();
    userRepository.save(user);
    assertThat(user.getId()).isNotNull();
    assertThat(user.isNew()).isFalse();
  }

  @Test
  void find_by_email_should_return_user_entity() {
    Optional<UserEntity> user = userRepository.findByEmail(EMAIL);

    assertThat(user).isPresent();
    assertThat(user.get()).isEqualTo(JOHN_DOE);
  }

  @Test
  void find_by_email_should_return_empty_optional() {
    Optional<UserEntity> user = userRepository.findByEmail(UNKNOWN_EMAIL);

    assertThat(user).isEmpty();
  }

  @Test
  void find_by_username_should_return_user_entity() {
    Optional<UserEntity> user = userRepository.findByUsername(USERNAME);

    assertThat(user).isPresent();
    assertThat(user.get()).isEqualTo(JOHN_DOE);
  }

  @Test
  void find_by_username_should_return_empty_optional() {
    Optional<UserEntity> user = userRepository.findByUsername(UNKNOWN_USERNAME);

    assertThat(user).isEmpty();
  }

  @Test
  void find_by_public_id_should_return_user_entity() {
    Optional<UserEntity> user = userRepository.findByPublicId(JOHN_DOE.getPublicId());

    assertThat(user).isPresent();
    assertThat(user.get()).isEqualTo(JOHN_DOE);
  }

  @Test
  void find_by_public_id_should_return_empty_optional() {
    Optional<UserEntity> user = userRepository.findByPublicId(UNKNOWN_USERNAME);

    assertThat(user).isEmpty();
  }

  @Test
  void exists_by_email_should_return_true_or_false() {
    assertThat(userRepository.existsByEmail(EMAIL)).isTrue();
    assertThat(userRepository.existsByEmail(UNKNOWN_EMAIL)).isFalse();
  }

  @Test
  void exists_by_username_should_return_true_or_false() {
    assertThat(userRepository.existsByUsername(USERNAME)).isTrue();
    assertThat(userRepository.existsByUsername(UNKNOWN_USERNAME)).isFalse();
  }

}
