package rocks.metaldetector.service.user;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.oidc.OidcScopes;

import java.util.Arrays;
import java.util.HashSet;

@Configuration
public class OAuth2UserServiceConfig {

  static final String GOOGLE_SCOPE_PROFILE = "https://www.googleapis.com/auth/userinfo.profile";
  static final String GOOGLE_SCOPE_EMAIL = "https://www.googleapis.com/auth/userinfo.email";

  @Bean
  public OidcUserService oidcUserService() {
    OidcUserService oidcUserService = new OidcUserService();
    oidcUserService.setAccessibleScopes(new HashSet<>(
        Arrays.asList(OidcScopes.PROFILE, OidcScopes.EMAIL, GOOGLE_SCOPE_PROFILE, GOOGLE_SCOPE_EMAIL)
    ));
    return oidcUserService;
  }
}
