package rocks.metaldetector.service.user;

import rocks.metaldetector.persistence.domain.user.OAuthUserEntity;

public class OAuthUserFactory {

  public static OAuthUserEntity createUser(String username, String email) {
    return OAuthUserEntity.builder()
        .username(username)
        .email(email)
        .avatar("avatar")
        .build();
  }
}
