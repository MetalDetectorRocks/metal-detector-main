package rocks.metaldetector.service.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rocks.metaldetector.persistence.domain.user.UserEntity;
import rocks.metaldetector.service.user.UserDto;
import rocks.metaldetector.service.user.UserFromTokenExtractor;
import rocks.metaldetector.service.user.UserService;
import rocks.metaldetector.service.user.events.OnRequestPasswordResetEvent;
import rocks.metaldetector.web.api.auth.InitResetPasswordRequest;
import rocks.metaldetector.web.api.auth.ResetPasswordRequest;

@Service
@RequiredArgsConstructor
public class ResetPasswordService {

  private final UserService userService;
  private final ApplicationEventPublisher eventPublisher;
  private final PasswordEncoder passwordEncoder;
  private final UserFromTokenExtractor userFromTokenExtractor;

  public void requestPasswordReset(InitResetPasswordRequest request) {
    UserDto userDto = userService.getUserByEmailOrUsername(request.getEmailOrUsername());
    eventPublisher.publishEvent(new OnRequestPasswordResetEvent(this, userDto));
  }

  @Transactional
  public void resetPassword(ResetPasswordRequest request) {
    UserEntity userEntity = userFromTokenExtractor.extractUserFromToken(request.getToken());
    userEntity.setPassword(passwordEncoder.encode(request.getNewPlainPassword()));
  }
}
