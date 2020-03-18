package rocks.metaldetector.config.resttemplate;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import rocks.metaldetector.discogs.DiscogsConfig;

import java.io.IOException;
import java.util.List;

@Slf4j
public class CustomClientHttpRequestInterceptor implements ClientHttpRequestInterceptor {

  private final DiscogsConfig discogsConfig;

  CustomClientHttpRequestInterceptor(DiscogsConfig discogsConfig) {
    this.discogsConfig = discogsConfig;
  }

  @Override
  public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
    request.getHeaders().setAccept(List.of(MediaType.APPLICATION_JSON));
    request.getHeaders().set("User-Agent", discogsConfig.getUserAgent());
    request.getHeaders().set("Authorization", "Discogs token=" + discogsConfig.getAccessToken());

    log.info("URI: {}", request.getURI());
    log.info("Headers: {}", request.getHeaders());

    return execution.execute(request, body);
  }
}
