package rocks.metaldetector.security;

import rocks.metaldetector.model.user.UserEntity;

import java.util.function.Supplier;

public interface CurrentUserSupplier extends Supplier<UserEntity> {

}
