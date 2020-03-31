package rocks.metaldetector.config.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import rocks.metaldetector.discogs.config.DiscogsCredentialsConfig;

import java.io.IOException;
import java.util.List;

@Slf4j
public class CustomClientHttpRequestInterceptor implements ClientHttpRequestInterceptor {

  private final DiscogsCredentialsConfig discogsCredentialsConfig;

  CustomClientHttpRequestInterceptor(DiscogsCredentialsConfig discogsCredentialsConfig) {
    this.discogsCredentialsConfig = discogsCredentialsConfig;
  }

  @Override
  public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
    request.getHeaders().setAccept(List.of(MediaType.APPLICATION_JSON));
    request.getHeaders().set("User-Agent", discogsCredentialsConfig.getUserAgent());
    request.getHeaders().set("Authorization", "Discogs token=" + discogsCredentialsConfig.getAccessToken());

    log.info("URI: {}", request.getURI());
    log.info("Headers: {}", request.getHeaders());

    return execution.execute(request, body);
  }
}
