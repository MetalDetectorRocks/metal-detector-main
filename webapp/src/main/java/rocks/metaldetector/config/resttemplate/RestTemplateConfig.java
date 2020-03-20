package rocks.metaldetector.config.resttemplate;

import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import rocks.metaldetector.discogs.config.DiscogsCredentialsConfig;

@Configuration
public class RestTemplateConfig {

  private final CloseableHttpClient httpClient;
  private final DiscogsCredentialsConfig discogsCredentialsConfig;

  @Autowired
  public RestTemplateConfig(CloseableHttpClient httpClient, DiscogsCredentialsConfig discogsCredentialsConfig) {
    this.httpClient = httpClient;
    this.discogsCredentialsConfig = discogsCredentialsConfig;
  }

  @Bean
  public HttpComponentsClientHttpRequestFactory clientHttpRequestFactory() {
    HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
    clientHttpRequestFactory.setHttpClient(httpClient);
    return clientHttpRequestFactory;
  }

  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplateBuilder()
            .requestFactory(this::clientHttpRequestFactory)
            .errorHandler(new CustomClientErrorHandler())
            .interceptors(new CustomClientHttpRequestInterceptor(discogsCredentialsConfig))
            .build();
  }
}