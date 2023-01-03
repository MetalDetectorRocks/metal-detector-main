package rocks.metaldetector.persistence.domain.user;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.PrePersist;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import rocks.metaldetector.persistence.domain.BaseEntity;
import rocks.metaldetector.support.infrastructure.ArtifactForFramework;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static rocks.metaldetector.persistence.domain.user.UserRole.ROLE_ADMINISTRATOR;
import static rocks.metaldetector.persistence.domain.user.UserRole.ROLE_USER;

@Getter
@EqualsAndHashCode(callSuper = true)
@Entity(name = "users")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class AbstractUserEntity extends BaseEntity implements UserDetails {

  @Column(name = "public_id", nullable = false, unique = true, updatable = false)
  private String publicId;

  @Column(name = "username", nullable = false, length = 50, unique = true, updatable = false)
  protected String username;

  @Column(name = "email", nullable = false, length = 120, unique = true)
  protected String email;

  @Column(name = "user_roles", nullable = false)
  @ElementCollection(fetch = FetchType.EAGER)
  @Enumerated(EnumType.STRING)
  protected Set<UserRole> userRoles;

  @Column(name = "enabled")
  protected boolean enabled;

  @Column(name = "avatar")
  protected String avatar;

  @Column(name = "account_non_expired")
  private boolean accountNonExpired = true;

  @Column(name = "account_non_locked")
  private boolean accountNonLocked = true;

  @Column(name = "credentials_non_expired")
  private boolean credentialsNonExpired = true;

  @Column(name = "last_login")
  private LocalDateTime lastLogin;

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

  public Set<UserRole> getUserRoles() {
    return Set.copyOf(this.userRoles);
  }

  public List<String> getUserRoleNames() {
    return getUserRoles().stream().map(UserRole::getDisplayName).collect(Collectors.toList());
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
    return userRoles.contains(ROLE_USER);
  }

  public boolean isAdministrator() {
    return userRoles.contains(ROLE_ADMINISTRATOR);
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

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return userRoles.stream().map(role -> new SimpleGrantedAuthority(role.name())).collect(Collectors.toList());
  }

  public UserRole getHighestRole() {
    return userRoles.contains(ROLE_ADMINISTRATOR) ? ROLE_ADMINISTRATOR : ROLE_USER;
  }

  public void setLastLogin(LocalDateTime lastLogin) {
    this.lastLogin = lastLogin;
  }
}
