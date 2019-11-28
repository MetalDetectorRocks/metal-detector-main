package com.metalr2.security;

import com.metalr2.model.user.UserEntity;

public interface CurrentUser {

  UserEntity getCurrentUserEntity();

}
