package com.metalr2.security;

import com.metalr2.model.user.UserEntity;

import java.util.function.Supplier;

public interface CurrentUserSupplier extends Supplier<UserEntity> {

}
