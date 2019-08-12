package com.metalr2.model.user;

import com.metalr2.model.AbstractEntity;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE) // for hibernate and model mapper
@EqualsAndHashCode(callSuper = true)
@Entity(name="users")
public class UserEntity extends AbstractEntity implements UserDetails {

  private static final int ENCRYPTED_PASSWORD_LENGTH = 60;

  @Column(name = "public_id", nullable = false, unique = true, updatable = false)
  private String publicId; // ToDo DanielW: test UUID later

  @Column(name = "username", nullable = false, length=50, unique = true, updatable = false)
  private String username;

  @Column(name = "email", nullable = false, length=120, unique = true)
  private String email;

  @Column(name = "encrypted_password", nullable = false, length = ENCRYPTED_PASSWORD_LENGTH)
  private String password;

  @Column(name = "user_roles", nullable = false)
  @ElementCollection(fetch = FetchType.EAGER)
  @Enumerated(EnumType.STRING)
  private Set<UserRole> userRoles;

  @Column(name = "enabled")
  private boolean enabled;

  @Column(name = "account_non_expired")
  private boolean accountNonExpired = true;

  @Column(name = "account_non_locked")
  private boolean accountNonLocked = true;

  @Column(name = "credentials_non_expired")
  private boolean credentialsNonExpired = true;

  @Builder
  public UserEntity(@NonNull String username, @NonNull String email, @NonNull String password,
                    @NonNull Set<UserRole> userRoles, boolean enabled) {
    this.username          = username;
    this.email             = email;
    this.password          = password;
    this.userRoles         = userRoles;
    this.enabled           = enabled;
  }

  @SuppressWarnings("unused") // used for model mapper
  public void setUsername(String username) {
    if (this.username != null && ! this.username.isEmpty()) {
      throw new UnsupportedOperationException("The username must not be changed.");
    }

    this.username = username;
  }

  // ToDo DanielW: use special data type for email address
  public void setEmail(String newEmail) {
    email = newEmail == null ? "" : newEmail;
  }

  @Override
  public String getPassword() {
    return password;
  }

  // ToDo DanielW: use special data type for encrypted password?
  public void setPassword(String newPassword) {
    if (newPassword == null || newPassword.length() != ENCRYPTED_PASSWORD_LENGTH) {
      throw new IllegalArgumentException("It seems that the new password has not been correctly encrypted.");
    }

    password = newPassword;
  }

  public void setUserRoles(Set<UserRole> newUserRoles) {
    if (newUserRoles == null || newUserRoles.isEmpty()) {
      throw new IllegalArgumentException("At least one user role must be set!");
    }

    userRoles = newUserRoles;
  }

  public boolean removeUserRole(UserRole userRole) {
    return userRoles.remove(userRole);
  }

  public boolean isUser() {
    return userRoles.contains(UserRole.USER);
  }

  public boolean isAdministrator() {
    return userRoles.contains(UserRole.ADMINISTRATOR);
  }

  public boolean isSuperUser() {
    return isUser() && isAdministrator();
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return null;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  @PrePersist
  private void generatePublicId() {
    if (publicId == null) {
      publicId = UUID.randomUUID().toString();
    }
  }

}
