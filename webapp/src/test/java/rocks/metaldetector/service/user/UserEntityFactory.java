package rocks.metaldetector.service.user;

import rocks.metaldetector.persistence.domain.user.UserEntity;
import rocks.metaldetector.persistence.domain.user.UserRole;

import java.util.Set;

public class UserEntityFactory {

  public static UserEntity createDefaultUser() {
    return createUser("JohnD", "john.doe@example.com");
  }

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

  static UserEntity createUser(String username, UserRole role, boolean enabled) {
    return UserEntity.builder()
            .username(username)
            .email(username + "@example.com")
            .password("$2a$10$2IevDskxEeSmy7Sy41Xl7.u22hTcw3saxQghS.bWaIx3NQrzKTvxK")
            .userRoles(Set.of(role))
            .enabled(enabled)
            .build();
  }
}
