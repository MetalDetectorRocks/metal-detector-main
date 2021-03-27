package rocks.metaldetector.security;

import rocks.metaldetector.persistence.domain.user.AbstractUserEntity;

import java.util.function.Supplier;

public interface CurrentUserSupplier extends Supplier<AbstractUserEntity> {
}
