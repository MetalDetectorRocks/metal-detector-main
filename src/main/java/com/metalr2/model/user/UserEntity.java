package com.metalr2.model.user;

import com.metalr2.model.AbstractEntity;
import lombok.*;

import javax.persistence.*;
import java.util.Set;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE) // for hibernate and model mapper
@EqualsAndHashCode(callSuper = true)
@Entity(name="users")
public class UserEntity extends AbstractEntity {

  @Column(name = "public_id", nullable = false, unique = true, updatable = false)
  private String publicId; // ToDo DanielW: test UUID later

  @Column(name = "username", nullable = false, length=50, unique = true, updatable = false)
  private String username;

  @Column(name = "email", nullable = false, length=120, unique = true)
  private String email;

  @Column(name = "encrypted_password", nullable = false, length = 60)
  private String encryptedPassword;

  @Column(name = "user_roles", nullable = false)
  @ElementCollection(fetch = FetchType.EAGER)
  @Enumerated(EnumType.STRING)
  private Set<UserRole> userRoles;

  @Column(name = "enabled")
  private boolean enabled;

  @Builder
  public UserEntity(@NonNull String username, @NonNull String email, @NonNull String encryptedPassword, @NonNull Set<UserRole> userRoles, boolean enabled) {
    this.username          = username;
    this.email             = email;
    this.encryptedPassword = encryptedPassword;
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

  public void setEncryptedPassword(String newEncryptedPassword) {
    encryptedPassword = newEncryptedPassword == null ? "" : newEncryptedPassword;
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

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
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

  @PrePersist
  private void generatePublicId() {
    if (publicId == null) {
      publicId = UUID.randomUUID().toString();
    }
  }

}
