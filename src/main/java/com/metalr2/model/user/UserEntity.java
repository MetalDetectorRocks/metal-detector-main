package com.metalr2.model.user;

import com.metalr2.model.ArtifactForFramework;
import com.metalr2.model.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.PrePersist;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.metalr2.model.user.UserRole.ROLE_ADMINISTRATOR;
import static com.metalr2.model.user.UserRole.ROLE_USER;

@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE) // for hibernate and model mapper
@EqualsAndHashCode(callSuper = true)
@Entity(name = "users")
public class UserEntity extends BaseEntity implements UserDetails {

  private static final int ENCRYPTED_PASSWORD_LENGTH = 60;

  @Column(name = "public_id", nullable = false, unique = true, updatable = false)
  private String publicId;

  @Column(name = "username", nullable = false, length = 50, unique = true, updatable = false)
  private String username;

  @Column(name = "email", nullable = false, length = 120, unique = true)
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
    this.username = username;
    this.email = email;
    this.password = password;
    this.userRoles = userRoles;
    this.enabled = enabled;
  }

  public void setPublicId(String newPublicId) {
    this.publicId = checkInitialValueAssignment(this.publicId, newPublicId);
  }

  @ArtifactForFramework // used for model mapper
  public void setUsername(String username) {
    if (this.username != null && !this.username.isEmpty()) {
      throw new UnsupportedOperationException("The username must not be changed.");
    }

    this.username = username;
  }

  public void setEmail(String newEmail) {
    email = newEmail == null ? "" : newEmail;
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

  public void setUserRoles(Set<UserRole> newUserRoles) {
    if (newUserRoles == null || newUserRoles.isEmpty()) {
      throw new IllegalArgumentException("At least one user role must be set!");
    }

    userRoles = newUserRoles;
  }

  public boolean removeUserRole(UserRole userRole) {
    if (userRoles.equals(Set.of(userRole))) {
      throw new IllegalStateException("At least one user role must be set!");
    }

    return userRoles.remove(userRole);
  }

  public boolean isUser() {
    return userRoles.contains(UserRole.ROLE_USER);
  }

  public boolean isAdministrator() {
    return userRoles.contains(UserRole.ROLE_ADMINISTRATOR);
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return userRoles.stream().map(role -> new SimpleGrantedAuthority(role.name())).collect(Collectors.toList());
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

  public UserRole getHighestRole() {
    return userRoles.contains(ROLE_ADMINISTRATOR) ? ROLE_ADMINISTRATOR : ROLE_USER;
  }
}
