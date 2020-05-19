package rocks.metaldetector.security;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import rocks.metaldetector.persistence.domain.user.UserEntity;

@Component
public class CurrentPublicUserIdSupplierImpl implements CurrentPublicUserIdSupplier {

  private static final String ANONYMOUS_USER_NAME = "anonymousUser";

  @Override
  public String get() {
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    return principal.equals(ANONYMOUS_USER_NAME) ? null : ((UserEntity) principal).getPublicId();
  }
}
