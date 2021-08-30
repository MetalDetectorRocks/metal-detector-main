package rocks.metaldetector.support;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.NonNull;
import rocks.metaldetector.support.infrastructure.WithSensitiveDataRemover;

import java.io.IOException;

@Slf4j
public class DefaultRequestLoggingInterceptor implements ClientHttpRequestInterceptor, WithSensitiveDataRemover {

  @Override
  @NonNull
  public ClientHttpResponse intercept(HttpRequest request, @NonNull byte[] body, ClientHttpRequestExecution execution) throws IOException {
    log.info("URI: {}", request.getURI());
    log.info("Headers: {}", removeTokenFromHeader(request.getHeaders().toString()));

    return execution.execute(request, body);
  }
}
