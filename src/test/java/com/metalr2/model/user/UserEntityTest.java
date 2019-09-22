package com.metalr2.model.user;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class UserEntityTest implements WithAssertions {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private BCryptPasswordEncoder passwordEncoder;

  @AfterEach
  void tearDown() {
    userRepository.deleteAll();
  }

  @Test
  void getPublicIdShouldReturnIdAfterPersisting() {
    UserEntity user = UserFactory.createUser("Test", "test@test.com");

    assertThat(user.getPublicId()).isNull();
    userRepository.save(user);
    assertThat(user.getPublicId()).isNotNull();
  }

  @Test
  void getIdShouldReturnIdAfterPersisting() {
    UserEntity user = UserFactory.createUser("Test", "test@test.com");

    assertThat(user.getId()).isNull();
    assertThat(user.isNew()).isTrue();
    userRepository.save(user);
    assertThat(user.getId()).isNotNull();
    assertThat(user.isNew()).isFalse();
  }

  @Test
  void setUsernameShouldThrowException() {
    UserEntity user = UserFactory.createUser("Test", "test@test.com");

    Throwable throwable = catchThrowable(() -> user.setUsername("new username"));

    assertThat(throwable).isInstanceOf(UnsupportedOperationException.class);
    assertThat(throwable).hasMessage("The username must not be changed.");
  }

  @Test
  void isUserShouldReturnTrueForUserOfRoleUser() {
    UserEntity user = UserFactory.createUser("User", "user@test.com");

    assertThat(user.isUser()).isTrue();
    assertThat(user.isAdministrator()).isFalse();
    assertThat(user.isSuperUser()).isFalse();
  }

  @Test
  void isAdministratorShouldReturnTrueForUserOfRoleAdministrator() {
    UserEntity user = UserFactory.createAdministrator();

    assertThat(user.isUser()).isFalse();
    assertThat(user.isAdministrator()).isTrue();
    assertThat(user.isSuperUser()).isFalse();
  }

  @Test
  void isSuperUserShouldReturnTrueForUserOfRoleSuperUser() {
    UserEntity user = UserFactory.createSuperUser();

    assertThat(user.isUser()).isTrue();
    assertThat(user.isAdministrator()).isTrue();
    assertThat(user.isSuperUser()).isTrue();
  }

  @Test
  void updateOfEmailShouldBePossible() {
    String initialEmail = "test@test.com";
    String newEmail     = "test-update@test.com";

    UserEntity user = UserFactory.createUser("user", initialEmail);
    assertThat(user.getEmail()).isEqualTo(initialEmail);

    user.setEmail(newEmail);
    assertThat(user.getEmail()).isEqualTo(newEmail);

    user.setEmail(null);
    assertThat(user.getEmail()).isEmpty();
  }

  @Test
  void updateOfPasswordWithValidValueShouldBePossible() {
    String newEncryptedPassword = passwordEncoder.encode("test1234");
    UserEntity user = UserFactory.createSuperUser();

    assertThat(user.getPassword()).isNotEqualTo(newEncryptedPassword);
    user.setPassword(newEncryptedPassword);
    assertThat(user.getPassword()).isEqualTo(newEncryptedPassword);
  }

  @Test
  void updatePasswordWithNullValueShouldThrowException() {
    UserEntity user = UserFactory.createSuperUser();

    Throwable setNullPassword = catchThrowable(() -> user.setPassword(null));

    assertThat(setNullPassword).isInstanceOf(IllegalArgumentException.class);
    assertThat(setNullPassword).hasMessage("It seems that the new password has not been correctly encrypted.");
  }

  @Test
  void updatePasswordWithEmptyValueShouldThrowException() {
    UserEntity user = UserFactory.createSuperUser();

    Throwable setEmptyPassword = catchThrowable(() -> user.setPassword(""));

    assertThat(setEmptyPassword).isInstanceOf(IllegalArgumentException.class);
    assertThat(setEmptyPassword).hasMessage("It seems that the new password has not been correctly encrypted.");
  }

  @Test
  void updateOfUserRolesShouldBePossible() {
    UserEntity user = UserFactory.createSuperUser();

    user.setUserRoles(UserRole.createAdministratorRole());
    assertThat(user.isAdministrator()).isTrue();

    user.setUserRoles(UserRole.createSuperUserRole());
    assertThat(user.isSuperUser()).isTrue();

    boolean removeResult = user.removeUserRole(UserRole.ROLE_ADMINISTRATOR);
    assertThat(user.isUser()).isTrue();
    assertThat(removeResult).isTrue();
  }

  @Test
  void updateOfUserRolesWithEmptySetShouldThrowException() {
    UserEntity user = UserFactory.createSuperUser();

    Throwable setEmptyCollection = catchThrowable(() -> user.setUserRoles(Collections.emptySet()));

    assertThat(setEmptyCollection).isInstanceOf(IllegalArgumentException.class);
    assertThat(setEmptyCollection).hasMessage("At least one user role must be set!");
  }

  @Test
  void updateOfUserRolesWithNullValueShouldThrowException() {
    UserEntity user = UserFactory.createSuperUser();

    Throwable setNullValue = catchThrowable(() -> user.setUserRoles(Collections.emptySet()));

    assertThat(setNullValue).isInstanceOf(IllegalArgumentException.class);
    assertThat(setNullValue).hasMessage("At least one user role must be set!");
  }

  @TestConfiguration
  static class UserEntityTestConfiguration {

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
      return new BCryptPasswordEncoder();
    }

  }

}

