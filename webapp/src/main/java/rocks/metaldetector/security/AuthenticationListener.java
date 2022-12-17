package rocks.metaldetector.security;

import lombok.AllArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import rocks.metaldetector.persistence.domain.user.UserEntity;
import rocks.metaldetector.service.user.UserDto;
import rocks.metaldetector.service.user.UserService;

import java.util.Map;

@Component
@AllArgsConstructor
public class AuthenticationListener {

  private final LoginAttemptService loginAttemptService;
  private final UserService userService;

  @EventListener
  public void onAuthenticationSuccess(AuthenticationSuccessEvent event) {
    WebAuthenticationDetails authenticationDetails = (WebAuthenticationDetails) event.getAuthentication().getDetails();
    loginAttemptService.loginSucceeded(DigestUtils.md5Hex(authenticationDetails.getRemoteAddress()));

    Object principal = event.getAuthentication().getPrincipal();

    if (principal instanceof UserEntity) {
      userService.persistSuccessfulLogin(((UserEntity) principal).getPublicId());
    }
    else if (principal instanceof OAuth2AuthenticatedPrincipal) {
      Map<String, Object> oauthTokenAttributes = ((OAuth2AuthenticatedPrincipal) principal).getAttributes();
      String emailAddress = (String) oauthTokenAttributes.get("email");
      UserDto user = userService.getUserByEmailOrUsername(emailAddress)
          .orElseThrow(() -> new IllegalStateException("OAuth user with email '" + emailAddress + "' not found"));
      userService.persistSuccessfulLogin(user.getPublicId());
    }
  }

  @EventListener
  public void onAuthenticationFailure(AuthenticationFailureBadCredentialsEvent event) {
    WebAuthenticationDetails authenticationDetails = (WebAuthenticationDetails) event.getAuthentication().getDetails();
    loginAttemptService.loginFailed(DigestUtils.md5Hex(authenticationDetails.getRemoteAddress()));
  }
}
