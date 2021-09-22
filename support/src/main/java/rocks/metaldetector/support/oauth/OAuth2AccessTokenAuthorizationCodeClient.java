package rocks.metaldetector.support.oauth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class OAuth2AccessTokenAuthorizationCodeClient implements OAuth2AccessTokenClient {

  private final OAuth2AuthorizedClientService oAuth2AuthorizedClientService;
  private final CurrentOAuthUserIdSupplier currentOAuthUserIdSupplier;
  private final OAuth2AuthorizedClientManager oAuth2AuthorizedClientManager;
  private String registrationId;

  @Override
  public String getAccessToken() {
    String userId = currentOAuthUserIdSupplier.get();
    OAuth2AuthorizedClient authorizedClient = oAuth2AuthorizedClientService.loadAuthorizedClient(registrationId, userId);

    if (authorizedClient == null || authorizedClient.getAccessToken().getExpiresAt() == null) {
      throw new IllegalArgumentException("No access token for client '" + userId + "' and registrationId '" + registrationId + "'");
    }

    if (authorizedClient.getAccessToken().getExpiresAt().isBefore(Instant.now())) {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      OAuth2AuthorizeRequest request = OAuth2AuthorizeRequest
          .withAuthorizedClient(authorizedClient)
          .principal(authentication)
          .build();
      OAuth2AuthorizedClient reAuthorizedClient = oAuth2AuthorizedClientManager.authorize(request);

      if (reAuthorizedClient == null) {
        throw new IllegalArgumentException("No refreshed access token for client '" + userId + "' and registrationId '" + registrationId + "'");
      }

      return reAuthorizedClient.getAccessToken().getTokenValue();
    }

    return authorizedClient.getAccessToken().getTokenValue();
  }

  @Override
  public void setRegistrationId(String registrationId) {
    this.registrationId = registrationId;
  }
}
