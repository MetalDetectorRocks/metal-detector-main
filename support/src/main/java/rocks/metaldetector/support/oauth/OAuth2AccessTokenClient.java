package rocks.metaldetector.support.oauth;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

@Component
@Scope(SCOPE_PROTOTYPE)
@RequiredArgsConstructor
public class OAuth2AccessTokenClient {

  static final long TOKEN_EXPIRATION_GRACE_PERIOD = 15;

  private final OAuth2AuthorizedClientManager authorizedClientManager;
  private final OAuth2AuthorizedClientService authorizedClientService;
  private final OAuth2AuthorizeRequestProvider authorizeRequestProvider;
  private final OAuth2AuthenticationProvider authenticationProvider;

  @Setter
  private String registrationId;
  @Setter
  private AuthorizationGrantType authorizationGrantType;

  public String getAccessToken() {
    Authentication currentAuthentication = authenticationProvider.provideForGrant(authorizationGrantType);
    OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient(registrationId, currentAuthentication.getName());

    if (isAuthorizationRequired(authorizedClient)) {
      OAuth2AuthorizeRequest authorizedRequest = authorizeRequestProvider.provideForGrant(authorizationGrantType, authorizedClient, registrationId);
      authorizedClient = authorizedClientManager.authorize(authorizedRequest);
      validateAuthorizedClient(authorizedClient, currentAuthentication);
      authorizedClientService.saveAuthorizedClient(authorizedClient, currentAuthentication);
    }
    return authorizedClient.getAccessToken().getTokenValue();
  }

  private boolean isAuthorizationRequired(OAuth2AuthorizedClient authorizedClient) {
    if (authorizedClient == null || authorizedClient.getAccessToken() == null
        || authorizedClient.getAccessToken().getExpiresAt() == null) {
      return true;
    }
    Instant expirationDate = authorizedClient.getAccessToken().getExpiresAt().minusSeconds(TOKEN_EXPIRATION_GRACE_PERIOD);
    return LocalDateTime.now().isAfter(LocalDateTime.ofInstant(expirationDate, ZoneOffset.ofHoursMinutes(1, 0)));
  }

  private void validateAuthorizedClient(OAuth2AuthorizedClient authorizedClient, Authentication authentication) {
    if (authorizedClient == null || authorizedClient.getAccessToken() == null ||
        authorizedClient.getAccessToken().getTokenValue() == null || authorizedClient.getAccessToken().getTokenValue().isBlank()) {
      throw new IllegalStateException("Could not authorize client '" + authentication.getName() + "' with registrationId '" + registrationId + "'");
    }
  }
}
