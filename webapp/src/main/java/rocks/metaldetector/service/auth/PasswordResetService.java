package rocks.metaldetector.service.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import rocks.metaldetector.service.user.UserDto;
import rocks.metaldetector.service.user.UserService;
import rocks.metaldetector.service.user.events.OnRequestPasswordResetEvent;
import rocks.metaldetector.web.api.auth.PasswordResetRequest;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

  private final UserService userService;
  private final ApplicationEventPublisher eventPublisher;

  public void requestPasswordReset(PasswordResetRequest request) {
    UserDto userDto = userService.getUserByEmailOrUsername(request.getEmailOrUsername());
    eventPublisher.publishEvent(new OnRequestPasswordResetEvent(this, userDto));
  }
}
