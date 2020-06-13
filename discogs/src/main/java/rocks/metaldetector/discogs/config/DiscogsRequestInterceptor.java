package rocks.metaldetector.discogs.config;

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
public class DiscogsRequestInterceptor implements ClientHttpRequestInterceptor, WithTokenRemover {

  private final DiscogsConfig discogsConfig;

  DiscogsRequestInterceptor(DiscogsConfig discogsConfig) {
    this.discogsConfig = discogsConfig;
  }

  @Override
  public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
    request.getHeaders().setAccept(List.of(MediaType.APPLICATION_JSON));
    request.getHeaders().set("User-Agent", discogsConfig.getUserAgent());
    request.getHeaders().set(AUTHORIZATION, "Discogs token=" + discogsConfig.getAccessToken());

    log.info("URI: {}", request.getURI());
    log.info("Headers: {}", removeTokenForLogging(request.getHeaders().toString()));

    return execution.execute(request, body);
  }
}
