package rocks.metaldetector.security;

import rocks.metaldetector.persistence.domain.user.UserEntity;

import java.util.function.Supplier;

public interface CurrentUserSupplier extends Supplier<UserEntity> {
}
