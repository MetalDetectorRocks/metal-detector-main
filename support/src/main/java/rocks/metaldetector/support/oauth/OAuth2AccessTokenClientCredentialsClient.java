package rocks.metaldetector.support.oauth;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;
import static org.springframework.security.core.authority.AuthorityUtils.createAuthorityList;

@Component
@Scope(SCOPE_PROTOTYPE)
@RequiredArgsConstructor
public class OAuth2AccessTokenClientCredentialsClient implements OAuth2AccessTokenClient {

  static final AnonymousAuthenticationToken PRINCIPAL = new AnonymousAuthenticationToken("key", "anonymous", createAuthorityList("ROLE_ANONYMOUS"));
  static final long TOKEN_EXPIRATION_GRACE_PERIOD = 15;

  private final OAuth2AuthorizedClientManager oAuth2AuthorizedClientManager;
  private final OAuth2AuthorizedClientService oAuth2AuthorizedClientService;
  private String registrationId;

  @Override
  public String getAccessToken() {
    OAuth2AuthorizedClient authorizedClient = oAuth2AuthorizedClientService.loadAuthorizedClient(registrationId, PRINCIPAL.getName());

    if (isAuthorizationRequired(authorizedClient)) {
      OAuth2AuthorizeRequest authorizedRequest = OAuth2AuthorizeRequest
          .withClientRegistrationId(registrationId)
          .principal(PRINCIPAL)
          .build();
      authorizedClient = oAuth2AuthorizedClientManager.authorize(authorizedRequest);
      if (authorizedClient == null || authorizedClient.getAccessToken() == null ||
          authorizedClient.getAccessToken().getTokenValue() == null || authorizedClient.getAccessToken().getTokenValue().isEmpty()) {
        throw new IllegalArgumentException("No access token for client '" + registrationId + "'");
      }
      oAuth2AuthorizedClientService.saveAuthorizedClient(authorizedClient, PRINCIPAL);
    }
    return authorizedClient.getAccessToken().getTokenValue();
  }

  @Override
  public void setRegistrationId(String registrationId) {
    this.registrationId = registrationId;
  }

  private boolean isAuthorizationRequired(OAuth2AuthorizedClient authorizedClient) {
    if (authorizedClient == null || authorizedClient.getAccessToken() == null
        || authorizedClient.getAccessToken().getExpiresAt() == null) {
      return true;
    }
    Instant expirationDate = authorizedClient.getAccessToken().getExpiresAt().minusSeconds(TOKEN_EXPIRATION_GRACE_PERIOD);
    return LocalDateTime.now().isAfter(LocalDateTime.ofInstant(expirationDate, ZoneOffset.ofHoursMinutes(1, 0)));
  }
}
