package rocks.metaldetector.support.oauth;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

@Component
public class CurrentOAuthUserIdSupplierImpl implements CurrentOAuthUserIdSupplier {

  public static final String GOOGLE_REGISTRATION_ID = "google";
  public static final String GOOGLE_USER_ID_ATTRIBUTE = "sub";

  @Override
  public String get() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String userId;

    if (authentication instanceof OAuth2AuthenticationToken) {
      String registrationId = ((OAuth2AuthenticationToken) authentication).getAuthorizedClientRegistrationId();
      if (registrationId != null && registrationId.equals(GOOGLE_REGISTRATION_ID)) {
        userId = ((OAuth2User) authentication.getPrincipal()).getAttribute(GOOGLE_USER_ID_ATTRIBUTE);
      } else {
        throw new IllegalStateException("registrationId '" + registrationId + "' not found");
      }
    }
    else {
      userId = authentication.getName();
    }

    return userId;
  }
}
