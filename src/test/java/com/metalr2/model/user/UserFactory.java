package com.metalr2.model.user;

public class UserFactory {

  public static UserEntity createUser(String username, String email) {
    return UserEntity.builder()
            .username(username)
            .email(email)
            .password("$2a$10$2IevDskxEeSmy7Sy41Xl7.u22hTcw3saxQghS.bWaIx3NQrzKTvxK")
            .userRoles(UserRole.createUserRole())
            .enabled(true)
            .build();
  }

  static UserEntity createAdministrator() {
    return UserEntity.builder()
            .username("super-user")
            .email("super-user@example.com")
            .password("$2a$10$2IevDskxEeSmy7Sy41Xl7.u22hTcw3saxQghS.bWaIx3NQrzKTvxK")
            .userRoles(UserRole.createAdministratorRole())
            .enabled(true)
            .build();
  }

  static UserEntity createSuperUser() {
    return UserEntity.builder()
            .username("super-user")
            .email("super-user@example.com")
            .password("$2a$10$2IevDskxEeSmy7Sy41Xl7.u22hTcw3saxQghS.bWaIx3NQrzKTvxK")
            .userRoles(UserRole.createSuperUserRole())
            .enabled(true)
            .build();
  }

}
