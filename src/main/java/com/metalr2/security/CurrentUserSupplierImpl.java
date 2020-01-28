package com.metalr2.security;

import com.metalr2.model.user.UserEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class CurrentUserSupplierImpl implements CurrentUserSupplier {

  private final String ANONYMOUS_USER_NAME = "anonymousUser";

  @Override
  public UserEntity get() {
    if (SecurityContextHolder.getContext().getAuthentication().getPrincipal().equals(ANONYMOUS_USER_NAME)){
      return null;
    }
    return (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
  }

}
