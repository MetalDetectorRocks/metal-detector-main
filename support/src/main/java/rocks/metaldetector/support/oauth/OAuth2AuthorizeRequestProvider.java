package rocks.metaldetector.support.oauth;

import lombok.AllArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.stereotype.Component;

import static org.springframework.security.oauth2.core.AuthorizationGrantType.AUTHORIZATION_CODE;
import static org.springframework.security.oauth2.core.AuthorizationGrantType.CLIENT_CREDENTIALS;

@Component
@AllArgsConstructor
public class OAuth2AuthorizeRequestProvider {

  private final OAuth2AuthenticationProvider oAuth2AuthenticationProvider;

  public OAuth2AuthorizeRequest provideForGrant(AuthorizationGrantType grantType, @Nullable OAuth2AuthorizedClient authorizedClient, String registrationId) {
    if (grantType.equals(CLIENT_CREDENTIALS)) {
      return OAuth2AuthorizeRequest
          .withClientRegistrationId(registrationId)
          .principal(oAuth2AuthenticationProvider.provideForGrant(grantType))
          .build();
    }
    if (grantType.equals(AUTHORIZATION_CODE)) {
      return OAuth2AuthorizeRequest
          .withAuthorizedClient(authorizedClient)
          .principal(oAuth2AuthenticationProvider.provideForGrant(grantType))
          .build();
    }
    throw new IllegalArgumentException("Invalid grant type: '" + grantType.getValue() + "'");
  }
}
