package com.metalr2.model.user;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class UserRepositoryTest {

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

    assertTrue(user.isPresent());
    assertEquals(JOHN_DOE, user.get());
  }

  @Test
  void findByEmailShouldReturnEmptyOptional() {
    Optional<UserEntity> user = userRepository.findByEmail(UNKNOWN_EMAIL);

    assertTrue(user.isEmpty());
  }

  @Test
  void findByUsernameShouldReturnUserEntity() {
    Optional<UserEntity> user = userRepository.findByUsername(USERNAME);

    assertTrue(user.isPresent());
    assertEquals(JOHN_DOE, user.get());
  }

  @Test
  void findByUsernameShouldReturnEmptyOptional() {
    Optional<UserEntity> user = userRepository.findByUsername(UNKNOWN_USERNAME);

    assertTrue(user.isEmpty());
  }

  @Test
  void findByPublicIdShouldReturnUserEntity() {
    Optional<UserEntity> user = userRepository.findByPublicId(JOHN_DOE.getPublicId());

    assertTrue(user.isPresent());
    assertEquals(JOHN_DOE, user.get());
  }

  @Test
  void findByPublicIdShouldReturnEmptyOptional() {
    Optional<UserEntity> user = userRepository.findByPublicId(UNKNOWN_USERNAME);

    assertTrue(user.isEmpty());
  }

  @Test
  void existsByEmailShouldReturnTrueOrFalse() {
    assertTrue(userRepository.existsByEmail(EMAIL));
    assertFalse(userRepository.existsByEmail(UNKNOWN_EMAIL));
  }

  @Test
  void existsByUsernameShouldReturnTrueOrFalse() {
    assertTrue(userRepository.existsByUsername(USERNAME));
    assertFalse(userRepository.existsByUsername(UNKNOWN_USERNAME));
  }

}
