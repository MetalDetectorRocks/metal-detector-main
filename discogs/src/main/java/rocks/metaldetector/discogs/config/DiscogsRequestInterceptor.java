package rocks.metaldetector.discogs.config;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.NonNull;
import rocks.metaldetector.support.infrastructure.WithSensitiveDataRemover;

import java.io.IOException;
import java.util.List;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Slf4j
@AllArgsConstructor
public class DiscogsRequestInterceptor implements ClientHttpRequestInterceptor, WithSensitiveDataRemover {

  static final String TOKEN_PREFIX = "Discogs token=";

  private final DiscogsConfig discogsConfig;

  @Override
  @NonNull
  public ClientHttpResponse intercept(HttpRequest request, @NonNull byte[] body, ClientHttpRequestExecution execution) throws IOException {
    request.getHeaders().setAccept(List.of(MediaType.APPLICATION_JSON));
    request.getHeaders().set("User-Agent", discogsConfig.getUserAgent());
    request.getHeaders().set(AUTHORIZATION, TOKEN_PREFIX + discogsConfig.getAccessToken());

    log.info("URI: {}", request.getURI());
    log.info("Headers: {}", removeTokenFromHeader(request.getHeaders().toString()));

    return execution.execute(request, body);
  }
}
