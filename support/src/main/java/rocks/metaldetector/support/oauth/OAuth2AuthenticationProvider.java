package rocks.metaldetector.support.oauth;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.stereotype.Component;

import static org.springframework.security.core.authority.AuthorityUtils.createAuthorityList;
import static org.springframework.security.oauth2.core.AuthorizationGrantType.AUTHORIZATION_CODE;
import static org.springframework.security.oauth2.core.AuthorizationGrantType.CLIENT_CREDENTIALS;

@Component
public class OAuth2AuthenticationProvider {

  protected static final AnonymousAuthenticationToken PRINCIPAL = new AnonymousAuthenticationToken("key", "anonymous", createAuthorityList("ROLE_ANONYMOUS"));

  public Authentication provideForGrant(AuthorizationGrantType grantType) {
    if (grantType.equals(CLIENT_CREDENTIALS)) {
      return PRINCIPAL;
    }
    if (grantType.equals(AUTHORIZATION_CODE)) {
      return SecurityContextHolder.getContext().getAuthentication();
    }
    throw new IllegalArgumentException("Invalid grant type: '" + grantType.getValue() + "'");
  }
}
