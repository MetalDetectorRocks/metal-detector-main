package rocks.metaldetector.security;

import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import rocks.metaldetector.persistence.domain.user.UserEntity;
import rocks.metaldetector.persistence.domain.user.UserRepository;

@Component
@AllArgsConstructor
public class CurrentUserSupplierImpl implements CurrentUserSupplier {

  private final UserRepository userRepository;

  @Override
  public UserEntity get() {
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    if (principal instanceof UserEntity) {
      return userRepository.findByPublicId(((UserEntity) principal).getPublicId()).orElseThrow(
              () -> new RuntimeException("should not happen: No user found in the database, although the Principal is a UserEntity")
      );
    }

    return null;
  }
}
