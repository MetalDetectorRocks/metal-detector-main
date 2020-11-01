package rocks.metaldetector.security;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import rocks.metaldetector.persistence.domain.user.UserEntity;

import java.util.List;

@Component
public class CurrentUserSupplierImpl implements CurrentUserSupplier {

  private static final List<Object> ANONYMOUS_USER_NAMES = List.of("anonymousUser", "anonymous");

  @Override
  public UserEntity get() {
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    return ANONYMOUS_USER_NAMES.contains(principal) ? null : ((UserEntity) principal);
  }
}
