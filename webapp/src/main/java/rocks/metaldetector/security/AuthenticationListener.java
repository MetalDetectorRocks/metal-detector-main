package rocks.metaldetector.security;

import lombok.AllArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;
import rocks.metaldetector.persistence.domain.user.UserEntity;
import rocks.metaldetector.service.user.UserService;

@Component
@AllArgsConstructor
public class AuthenticationListener {

  public static final int MAX_FAILED_LOGINS = 5;

  private final UserService userService;

  @EventListener
  public void onAuthenticationSuccess(AuthenticationSuccessEvent event) {
    userService.persistSuccessfulLogin(((UserEntity) event.getAuthentication().getPrincipal()).getPublicId());
  }

  @EventListener
  public void onAuthenticationFailure(AbstractAuthenticationFailureEvent event) {
    userService.handleFailedLogin((String) event.getAuthentication().getPrincipal());
  }
}
