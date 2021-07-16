package rocks.metaldetector.support.oauth;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

public class OAuth2ClientInterceptor implements ClientHttpRequestInterceptor {

  private final OAuth2AccessTokenClient tokenClient;

  public OAuth2ClientInterceptor(OAuth2AccessTokenClient tokenClient) {
    this.tokenClient = tokenClient;
  }

  @Override
  public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
    String accessTokenValue = tokenClient.getAccessToken();
    request.getHeaders().setBearerAuth(accessTokenValue);
    return execution.execute(request, body);
  }
}
