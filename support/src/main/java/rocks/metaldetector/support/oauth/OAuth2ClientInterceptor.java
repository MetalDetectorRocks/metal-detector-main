package rocks.metaldetector.support.oauth;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

public class OAuth2ClientInterceptor implements ClientHttpRequestInterceptor {

  private final OAuth2AccessTokenSupplier tokenSupplier;

  public OAuth2ClientInterceptor(OAuth2AccessTokenSupplier tokenSupplier) {
    this.tokenSupplier = tokenSupplier;
  }

  @Override
  public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
    String accessTokenValue = tokenSupplier.get();
    request.getHeaders().setBearerAuth(accessTokenValue);
    return execution.execute(request, body);
  }
}
