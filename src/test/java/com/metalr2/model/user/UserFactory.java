package com.metalr2.model.user;

class UserFactory {

  static UserEntity createUser(String username, String email) {
    return UserEntity.builder()
            .username(username)
            .email(email)
            .encryptedPassword("$2a$10$2IevDskxEeSmy7Sy41Xl7.u22hTcw3saxQghS.bWaIx3NQrzKTvxK")
            .userRoles(UserRole.createUserRole())
            .enabled(true)
            .build();
  }

  static UserEntity createAdministrator(String username, String email) {
    return UserEntity.builder()
            .username(username)
            .email(email)
            .encryptedPassword("$2a$10$2IevDskxEeSmy7Sy41Xl7.u22hTcw3saxQghS.bWaIx3NQrzKTvxK")
            .userRoles(UserRole.createAdministratorRole())
            .enabled(true)
            .build();
  }

  static UserEntity createSuperUser(String username, String email) {
    return UserEntity.builder()
            .username(username)
            .email(email)
            .encryptedPassword("$2a$10$2IevDskxEeSmy7Sy41Xl7.u22hTcw3saxQghS.bWaIx3NQrzKTvxK")
            .userRoles(UserRole.createSuperUserRole())
            .enabled(true)
            .build();
  }

}
