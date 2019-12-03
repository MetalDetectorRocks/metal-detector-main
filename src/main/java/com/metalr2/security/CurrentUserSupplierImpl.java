package com.metalr2.security;

import com.metalr2.model.user.UserEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class CurrentUserSupplierImpl implements CurrentUserSupplier {

  @Override
  public UserEntity get() {
    return (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
  }

}
