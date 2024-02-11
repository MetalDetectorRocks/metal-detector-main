package rocks.metaldetector.service.oauthAuthorizationState;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rocks.metaldetector.persistence.domain.user.AbstractUserEntity;
import rocks.metaldetector.persistence.domain.user.OAuthAuthorizationStateEntity;
import rocks.metaldetector.persistence.domain.user.OAuthAuthorizationStateRepository;

@Service
@AllArgsConstructor
public class OAuthAuthorizationStateServiceImpl implements OAuthAuthorizationStateService {

  private final OAuthAuthorizationStateRepository authorizationStateRepository;

  @Override
  @Transactional(readOnly = true)
  public AbstractUserEntity findUserByState(String state) {
    return authorizationStateRepository.findByState(state)
        .map(OAuthAuthorizationStateEntity::getUser)
        .orElse(null);
  }

  @Override
  @Transactional
  public void deleteByState(String state) {
    authorizationStateRepository.deleteByState(state);
  }
}
