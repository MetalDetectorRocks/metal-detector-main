package rocks.metaldetector.butler.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import rocks.metaldetector.support.infrastructure.WithTokenRemover;

import java.io.IOException;
import java.util.List;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Slf4j
public class ButlerRequestInterceptor implements ClientHttpRequestInterceptor, WithTokenRemover {

  static final String TOKEN_PREFIX = "Bearer ";

  private final ButlerConfig butlerConfig;

  ButlerRequestInterceptor(ButlerConfig butlerConfig) {
    this.butlerConfig = butlerConfig;
  }

  @Override
  public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
    request.getHeaders().setAccept(List.of(MediaType.APPLICATION_JSON));
    request.getHeaders().set(AUTHORIZATION, TOKEN_PREFIX + butlerConfig.getAccessToken());

    log.info("URI: {}", request.getURI());
    log.info("Headers: {}", removeTokenForLogging(request.getHeaders().toString()));

    return execution.execute(request, body);
  }
}
