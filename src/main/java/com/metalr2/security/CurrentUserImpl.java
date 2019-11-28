package com.metalr2.security;

import com.metalr2.model.user.UserEntity;
import org.springframework.security.core.context.SecurityContextHolder;

public class CurrentUserImpl implements CurrentUser {

  @Override
  public UserEntity getCurrentUserEntity() {
    return (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
  }

}
