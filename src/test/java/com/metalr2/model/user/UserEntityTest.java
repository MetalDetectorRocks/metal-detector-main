package com.metalr2.model.user;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.Collections;

@SpringJUnitConfig
class UserEntityTest implements WithAssertions {

  @Autowired
  private BCryptPasswordEncoder passwordEncoder;

  @DisplayName("Username tests")
  @Nested
  class UsernameTests {

    @Test
    @DisplayName("setUsername() should throw exception when try to update the current username")
    void set_username_should_throw_exception() {
      UserEntity user = UserFactory.createUser("Test", "test@test.com");

      Throwable throwable = catchThrowable(() -> user.setUsername("new username"));

      assertThat(throwable).isInstanceOf(UnsupportedOperationException.class);
      assertThat(throwable).hasMessage("The username must not be changed.");
    }

  }

  @DisplayName("User role tests")
  @Nested
  class UserRoleTests {

    @Test
    @DisplayName("isUser() should return true for user of role 'ROLE_USER'")
    void is_user_should_return_true_for_user_of_role_user() {
      UserEntity user = UserFactory.createUser("User", "user@test.com");

      assertThat(user.isUser()).isTrue();
      assertThat(user.isAdministrator()).isFalse();
      assertThat(user.isSuperUser()).isFalse();
    }

    @Test
    @DisplayName("isAdministrator() should return true for user of role 'ROLE_ADMINISTRATOR'")
    void is_administrator_should_return_true_for_user_of_role_administrator() {
      UserEntity user = UserFactory.createAdministrator();

      assertThat(user.isUser()).isFalse();
      assertThat(user.isAdministrator()).isTrue();
      assertThat(user.isSuperUser()).isFalse();
    }

    @Test
    @DisplayName("isSuperUser() should return true for user of role 'ROLE_USER' and 'ROLE_ADMINISTRATOR'")
    void is_super_user_should_return_true_for_user_of_role_super_user() {
      UserEntity user = UserFactory.createSuperUser();

      assertThat(user.isUser()).isTrue();
      assertThat(user.isAdministrator()).isTrue();
      assertThat(user.isSuperUser()).isTrue();
    }

    @Test
    @DisplayName("adding and removing roles should be possible")
    void update_of_user_roles_should_be_possible() {
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
    @DisplayName("updating the user roles with an empty set should throw an exception")
    void update_of_user_roles_with_empty_set_should_throw_exception() {
      UserEntity user = UserFactory.createSuperUser();

      Throwable setEmptyCollection = catchThrowable(() -> user.setUserRoles(Collections.emptySet()));

      assertThat(setEmptyCollection).isInstanceOf(IllegalArgumentException.class);
      assertThat(setEmptyCollection).hasMessage("At least one user role must be set!");
    }

    @Test
    @DisplayName("updating the user roles with an null set should throw an exception")
    void update_of_user_roles_with_null_value_should_throw_exception() {
      UserEntity user = UserFactory.createSuperUser();

      Throwable setNullValue = catchThrowable(() -> user.setUserRoles(Collections.emptySet()));

      assertThat(setNullValue).isInstanceOf(IllegalArgumentException.class);
      assertThat(setNullValue).hasMessage("At least one user role must be set!");
    }

  }

  @DisplayName("Email tests")
  @Nested
  class EmailTests {

    @Test
    @DisplayName("updating the email should be possible")
    void update_of_email_should_be_possible() {
      String initialEmail = "test@test.com";
      String newEmail     = "test-update@test.com";

      UserEntity user = UserFactory.createUser("user", initialEmail);
      assertThat(user.getEmail()).isEqualTo(initialEmail);

      user.setEmail(newEmail);
      assertThat(user.getEmail()).isEqualTo(newEmail);

      user.setEmail(null);
      assertThat(user.getEmail()).isEmpty();
    }

  }

  @DisplayName("Password tests")
  @Nested
  class PasswordTests {

    @Test
    @DisplayName("updating the password with valid values should be possible")
    void update_of_password_with_valid_value_should_be_possible() {
      String newEncryptedPassword = passwordEncoder.encode("test1234");
      UserEntity user = UserFactory.createSuperUser();

      assertThat(user.getPassword()).isNotEqualTo(newEncryptedPassword);
      user.setPassword(newEncryptedPassword);
      assertThat(user.getPassword()).isEqualTo(newEncryptedPassword);
    }

    @Test
    @DisplayName("updating the password with a null value should throw an exception")
    void update_password_with_null_value_should_throw_exception() {
      UserEntity user = UserFactory.createSuperUser();

      Throwable setNullPassword = catchThrowable(() -> user.setPassword(null));

      assertThat(setNullPassword).isInstanceOf(IllegalArgumentException.class);
      assertThat(setNullPassword).hasMessage("It seems that the new password has not been correctly encrypted.");
    }

    @Test
    @DisplayName("updating the password with an empty value should throw an exception")
    void update_password_with_empty_value_should_throw_exception() {
      UserEntity user = UserFactory.createSuperUser();

      Throwable setEmptyPassword = catchThrowable(() -> user.setPassword(""));

      assertThat(setEmptyPassword).isInstanceOf(IllegalArgumentException.class);
      assertThat(setEmptyPassword).hasMessage("It seems that the new password has not been correctly encrypted.");
    }

  }

  @TestConfiguration
  static class UserEntityTestConfiguration {

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
      return new BCryptPasswordEncoder();
    }

  }

}
