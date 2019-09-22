package com.metalr2.model.user;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class UserRepositoryTest implements WithAssertions {

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
  void findByEmailShouldReturnUserEntity() {
    Optional<UserEntity> user = userRepository.findByEmail(EMAIL);

    assertThat(user).isPresent();
    assertThat(user.get()).isEqualTo(JOHN_DOE);
  }

  @Test
  void findByEmailShouldReturnEmptyOptional() {
    Optional<UserEntity> user = userRepository.findByEmail(UNKNOWN_EMAIL);

    assertThat(user).isEmpty();
  }

  @Test
  void findByUsernameShouldReturnUserEntity() {
    Optional<UserEntity> user = userRepository.findByUsername(USERNAME);

    assertThat(user).isPresent();
    assertThat(user.get()).isEqualTo(JOHN_DOE);
  }

  @Test
  void findByUsernameShouldReturnEmptyOptional() {
    Optional<UserEntity> user = userRepository.findByUsername(UNKNOWN_USERNAME);

    assertThat(user).isEmpty();
  }

  @Test
  void findByPublicIdShouldReturnUserEntity() {
    Optional<UserEntity> user = userRepository.findByPublicId(JOHN_DOE.getPublicId());

    assertThat(user).isPresent();
    assertThat(user.get()).isEqualTo(JOHN_DOE);
  }

  @Test
  void findByPublicIdShouldReturnEmptyOptional() {
    Optional<UserEntity> user = userRepository.findByPublicId(UNKNOWN_USERNAME);

    assertThat(user).isEmpty();
  }

  @Test
  void existsByEmailShouldReturnTrueOrFalse() {
    assertThat(userRepository.existsByEmail(EMAIL)).isTrue();
    assertThat(userRepository.existsByEmail(UNKNOWN_EMAIL)).isFalse();
  }

  @Test
  void existsByUsernameShouldReturnTrueOrFalse() {
    assertThat(userRepository.existsByUsername(USERNAME)).isTrue();
    assertThat(userRepository.existsByUsername(UNKNOWN_USERNAME)).isFalse();
  }

}
