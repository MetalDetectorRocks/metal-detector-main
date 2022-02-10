package rocks.metaldetector.security;

import rocks.metaldetector.persistence.domain.user.AbstractUserEntity;

public interface AuthenticationFacade {

  boolean isAuthenticated();

  AbstractUserEntity getCurrentUser();
}
