package rocks.metaldetector.security;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import rocks.metaldetector.model.user.UserEntity;

@Component
public class CurrentUserSupplierImpl implements CurrentUserSupplier {

  private static final String ANONYMOUS_USER_NAME = "anonymousUser";

  @Override
  public UserEntity get() {
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    return principal.equals(ANONYMOUS_USER_NAME) ? null : (UserEntity) principal;
  }

}
