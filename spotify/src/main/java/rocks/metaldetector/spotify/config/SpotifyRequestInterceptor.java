package rocks.metaldetector.spotify.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import rocks.metaldetector.support.infrastructure.WithTokenRemover;

import java.io.IOException;

@Slf4j
public class SpotifyRequestInterceptor implements ClientHttpRequestInterceptor, WithTokenRemover {

  @Override
  public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
    log.info("URI: {}", request.getURI());
    log.info("Headers: {}", removeTokenForLogging(request.getHeaders().toString()));

    return execution.execute(request, body);
  }
}
