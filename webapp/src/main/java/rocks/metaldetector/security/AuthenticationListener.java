package rocks.metaldetector.security;

import lombok.AllArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import rocks.metaldetector.persistence.domain.user.UserEntity;
import rocks.metaldetector.service.user.UserService;

@Component
@AllArgsConstructor
public class AuthenticationListener {

  private final LoginAttemptService loginAttemptService;
  private final UserService userService;

  @EventListener
  public void onAuthenticationSuccess(AuthenticationSuccessEvent event) {
    WebAuthenticationDetails authenticationDetails = (WebAuthenticationDetails) event.getAuthentication().getDetails();
    loginAttemptService.loginSucceeded(DigestUtils.md5Hex(authenticationDetails.getRemoteAddress()));

    userService.persistSuccessfulLogin(((UserEntity) event.getAuthentication().getPrincipal()).getPublicId());
  }

  @EventListener
  public void onAuthenticationFailure(AuthenticationFailureBadCredentialsEvent event) {
    WebAuthenticationDetails authenticationDetails = (WebAuthenticationDetails) event.getAuthentication().getDetails();
    loginAttemptService.loginFailed(DigestUtils.md5Hex(authenticationDetails.getRemoteAddress()));
  }
}
