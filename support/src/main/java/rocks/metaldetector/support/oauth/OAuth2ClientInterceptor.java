package rocks.metaldetector.support.oauth;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;

import java.io.IOException;

import static org.springframework.security.core.authority.AuthorityUtils.createAuthorityList;

public class OAuth2ClientInterceptor implements ClientHttpRequestInterceptor {

  private static final AnonymousAuthenticationToken PRINCIPAL = new AnonymousAuthenticationToken("key", "anonymous", createAuthorityList("ROLE_ANONYMOUS"));

  private final OAuth2AuthorizedClientManager manager;
  private final String registrationId;

  public OAuth2ClientInterceptor(OAuth2AuthorizedClientManager manager, String registrationId) {
    this.manager = manager;
    this.registrationId = registrationId;
  }

  @Override
  public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
    OAuth2AuthorizeRequest authorizedRequest = OAuth2AuthorizeRequest
        .withClientRegistrationId(registrationId)
        .principal(PRINCIPAL)
        .build();
    OAuth2AuthorizedClient authorizedClient = manager.authorize(authorizedRequest);
    if (authorizedClient == null || authorizedClient.getAccessToken() == null ||
        authorizedClient.getAccessToken().getTokenValue() == null || authorizedClient.getAccessToken().getTokenValue().isEmpty()) {
      throw new IllegalArgumentException("No access token for client '" + registrationId + "'");
    }
    request.getHeaders().setBearerAuth(authorizedClient.getAccessToken().getTokenValue());
    return execution.execute(request, body);
  }
}
