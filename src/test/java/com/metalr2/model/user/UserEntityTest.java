package com.metalr2.model.user;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class UserEntityTest {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private BCryptPasswordEncoder passwordEncoder;

  @EnableJpaAuditing
  @TestConfiguration
  static class MyTestConfiguration {

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
      return new BCryptPasswordEncoder();
    }

  }

  @AfterEach
  void tearDown() {
    userRepository.deleteAll();
  }

  @Test
  void getPublicIdShouldReturnIdAfterPersisting() {
    UserEntity user = UserFactory.createUser("Test", "test@test.com");

    assertNull(user.getPublicId());
    userRepository.save(user);
    assertNotNull(user.getPublicId());
  }

  @Test
  void getIdShouldReturnIdAfterPersisting() {
    UserEntity user = UserFactory.createUser("Test", "test@test.com");

    assertNull(user.getId());
    assertTrue(user.isNew());
    userRepository.save(user);
    assertNotNull(user.getId());
  }

  @Test
  void setUsernameShouldThrowException() {
    UserEntity user = UserFactory.createUser("Test", "test@test.com");

    assertThrows(UnsupportedOperationException.class, () -> user.setUsername("new username"));
  }

  @Test
  void isUserShouldReturnTrueForUserOfRoleUser() {
    UserEntity user = UserFactory.createUser("User", "user@test.com");

    assertTrue(user.isUser());
    assertFalse(user.isAdministrator());
    assertFalse(user.isSuperUser());
  }

  @Test
  void isAdministratorShouldReturnTrueForUserOfRoleAdministrator() {
    UserEntity user = UserFactory.createAdministrator();

    assertFalse(user.isUser());
    assertTrue(user.isAdministrator());
    assertFalse(user.isSuperUser());
  }

  @Test
  void isSuperUserShouldReturnTrueForUserOfRoleSuperUser() {
    UserEntity user = UserFactory.createSuperUser();

    assertTrue(user.isUser());
    assertTrue(user.isAdministrator());
    assertTrue(user.isSuperUser());
  }

  @Test
  void updateOfEmailShouldBePossible() {
    String initialEmail = "test@test.com";
    String newEmail     = "test-update@test.com";

    UserEntity user = UserFactory.createUser("user", initialEmail);
    assertEquals(initialEmail, user.getEmail());

    user.setEmail(newEmail);
    assertEquals(newEmail, user.getEmail());

    user.setEmail(null);
    assertEquals("", user.getEmail());
  }

  @Test
  void updateOfPasswordShouldBePossible() {
    String newEncryptedPassword = passwordEncoder.encode("test1234");
    UserEntity user = UserFactory.createSuperUser();

    assertNotNull(user.getPassword());
    user.setPassword(newEncryptedPassword);
    assertEquals(newEncryptedPassword, user.getPassword());

    assertThrows(IllegalArgumentException.class, () -> user.setPassword(null));
    assertThrows(IllegalArgumentException.class, () -> user.setPassword(""));
  }

  @Test
  void updateOfUserRolesShouldBePossible() {
    UserEntity user = UserFactory.createSuperUser();

    user.setUserRoles(UserRole.createAdministratorRole());
    assertTrue(user.isAdministrator());

    user.setUserRoles(UserRole.createSuperUserRole());
    assertTrue(user.isSuperUser());

    boolean removeResult = user.removeUserRole(UserRole.ROLE_ADMINISTRATOR);
    assertTrue(user.isUser());
    assertTrue(removeResult);

    assertThrows(IllegalArgumentException.class, () -> user.setUserRoles(Collections.emptySet()));
    assertThrows(IllegalArgumentException.class, () -> user.setUserRoles(null));
  }

}
