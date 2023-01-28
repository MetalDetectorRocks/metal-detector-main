package rocks.metaldetector.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import rocks.metaldetector.persistence.domain.user.UserEntity;
import rocks.metaldetector.persistence.domain.user.UserRepository;
import rocks.metaldetector.support.JwtsSupport;
import rocks.metaldetector.support.exceptions.ResourceNotFoundException;

import static rocks.metaldetector.service.user.UserErrorMessages.USER_WITH_ID_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class UserFromTokenExtractor {

  private final JwtsSupport jwtsSupport;
  private final UserRepository userRepository;

  public UserEntity extractUserFromToken(String token) {
    var claims = jwtsSupport.getClaims(token);
    return (UserEntity) userRepository.findByPublicId(claims.getSubject())
        .orElseThrow(() -> new ResourceNotFoundException(USER_WITH_ID_NOT_FOUND.toDisplayString()));
  }
}
