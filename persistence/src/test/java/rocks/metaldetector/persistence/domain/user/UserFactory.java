package rocks.metaldetector.persistence.domain.user;

import java.util.Set;

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

  public static UserEntity createUser(String username, String email, String encryptedPassword) {
    return UserEntity.builder()
            .username(username)
            .email(email)
            .password(encryptedPassword)
            .userRoles(UserRole.createUserRole())
            .enabled(true)
            .build();
  }

  public static UserEntity createUser(String username, UserRole role, boolean enabled) {
    return UserEntity.builder()
            .username(username)
            .email(username + "@example.com")
            .password("$2a$10$2IevDskxEeSmy7Sy41Xl7.u22hTcw3saxQghS.bWaIx3NQrzKTvxK")
            .userRoles(Set.of(role))
            .enabled(enabled)
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
}
