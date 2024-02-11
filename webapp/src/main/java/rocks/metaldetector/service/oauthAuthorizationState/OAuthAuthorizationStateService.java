package rocks.metaldetector.service.oauthAuthorizationState;

import rocks.metaldetector.persistence.domain.user.AbstractUserEntity;

public interface OAuthAuthorizationStateService {

  AbstractUserEntity findUserByState(String state);

  void deleteByState(String state);
}
