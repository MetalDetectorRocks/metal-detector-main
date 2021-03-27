package rocks.metaldetector.persistence.domain.user;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.util.Set;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // for hibernate and model mapper
@EqualsAndHashCode(callSuper = true)
@Entity(name = "native_users")
public class UserEntity extends AbstractUserEntity {

  private static final int ENCRYPTED_PASSWORD_LENGTH = 60;

  @Column(name = "encrypted_password", length = ENCRYPTED_PASSWORD_LENGTH)
  private String password;

  @Builder
  public UserEntity(@NonNull String username, @NonNull String email, @NonNull String password,
                    @NonNull Set<UserRole> userRoles, boolean enabled) {
    this.username = username;
    this.email = email;
    this.password = password;
    this.userRoles = userRoles;
    this.enabled = enabled;
  }

  @Override
  public String getPassword() {
    return password;
  }

  public void setPassword(String newPassword) {
    if (newPassword == null || newPassword.length() != ENCRYPTED_PASSWORD_LENGTH) {
      throw new IllegalArgumentException("It seems that the new password has not been correctly encrypted.");
    }

    password = newPassword;
  }
}
