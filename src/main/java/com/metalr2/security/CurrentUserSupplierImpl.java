package com.metalr2.security;

import com.metalr2.model.user.UserEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class CurrentUserSupplierImpl implements CurrentUserSupplier {

  private static final String ANONYMOUS_USER_NAME = "anonymousUser";

  @Override
  public UserEntity get() {
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    return principal.equals(ANONYMOUS_USER_NAME) ? null : (UserEntity) principal;
  }

}
