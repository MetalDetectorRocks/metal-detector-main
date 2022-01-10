package rocks.metaldetector.butler.config;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import rocks.metaldetector.support.oauth.OAuth2AccessTokenClient;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

public class ButlerOAuth2ClientInterceptor implements ClientHttpRequestInterceptor {

  protected static final String ADMINISTRATOR_AUTHORITY = "ROLE_ADMINISTRATOR";
  protected static final String USER_AUTHORITY = "ROLE_USER";
  protected static final String ANONYMOUS_AUTHORITY = "ROLE_ANONYMOUS";

  private final OAuth2AccessTokenClient userTokenClient;
  private final OAuth2AccessTokenClient adminTokenClient;

  public ButlerOAuth2ClientInterceptor(OAuth2AccessTokenClient userTokenClient, OAuth2AccessTokenClient adminTokenClient) {
    this.userTokenClient = userTokenClient;
    this.adminTokenClient = adminTokenClient;
  }

  @Override
  public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
    Authentication currentAuthentication = SecurityContextHolder.getContext().getAuthentication();
    Set<String> grantedAuthorities = currentAuthentication.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .collect(Collectors.toSet());
    String accessTokenValue;
    if (grantedAuthorities.contains(ADMINISTRATOR_AUTHORITY)) {
      accessTokenValue = adminTokenClient.getAccessToken();
    }
    else if (grantedAuthorities.contains(USER_AUTHORITY) || grantedAuthorities.contains(ANONYMOUS_AUTHORITY)) {
      accessTokenValue = userTokenClient.getAccessToken();
    } else {
      throw new AccessDeniedException("No authorities present for principal '" + currentAuthentication.getName() + "'");
    }
    request.getHeaders().setBearerAuth(accessTokenValue);
    return execution.execute(request, body);
  }
}
