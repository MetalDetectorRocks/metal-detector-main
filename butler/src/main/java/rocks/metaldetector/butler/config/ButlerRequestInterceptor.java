package rocks.metaldetector.butler.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import rocks.metaldetector.support.infrastructure.WithSensitiveDataRemover;

import java.io.IOException;

@Slf4j
public class ButlerRequestInterceptor implements ClientHttpRequestInterceptor, WithSensitiveDataRemover {

  private final ButlerConfig butlerConfig;

  ButlerRequestInterceptor(ButlerConfig butlerConfig) {
    this.butlerConfig = butlerConfig;
  }

  @Override
  public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
    request.getHeaders().setBearerAuth(butlerConfig.getAccessToken());

    return execution.execute(request, body);
  }
}
