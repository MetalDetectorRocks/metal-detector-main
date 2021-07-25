package rocks.metaldetector.support.oauth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OAuth2AccessTokenAuthorizationCodeClient implements OAuth2AccessTokenClient {

  private final OAuth2AuthorizedClientService oAuth2AuthorizedClientService;
  private String registrationId;

  @Override
  public String getAccessToken() {
    OAuth2AuthorizedClient authorizedClient = oAuth2AuthorizedClientService.loadAuthorizedClient(registrationId, SecurityContextHolder.getContext().getAuthentication().getName());
    if (authorizedClient == null || authorizedClient.getAccessToken() == null ||
        authorizedClient.getAccessToken().getTokenValue() == null || authorizedClient.getAccessToken().getTokenValue().isEmpty()) {
      throw new IllegalArgumentException("No access token for client '" + registrationId + "'");
    }
    return authorizedClient.getAccessToken().getTokenValue();
  }

  @Override
  public void setRegistrationId(String registrationId) {
    this.registrationId = registrationId;
  }
}
