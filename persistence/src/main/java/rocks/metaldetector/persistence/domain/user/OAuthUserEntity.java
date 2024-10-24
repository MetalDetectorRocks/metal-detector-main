package rocks.metaldetector.persistence.domain.user;

import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.Set;

import static rocks.metaldetector.persistence.domain.user.UserRole.ROLE_USER;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // for hibernate and model mapper
@EqualsAndHashCode(callSuper = true)
@Entity(name = "oauth_users")
public class OAuthUserEntity extends AbstractUserEntity {

  @Builder
  public OAuthUserEntity(@NonNull String username, @NonNull String email, String avatar) {
    this.username = username;
    this.email = email;
    this.avatar = avatar;
    this.userRoles = Set.of(ROLE_USER);
    this.enabled = true;
  }

  @Override
  public String getPassword() {
    return null;
  }
}
