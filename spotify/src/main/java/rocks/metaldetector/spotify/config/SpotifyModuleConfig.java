package rocks.metaldetector.spotify.config;

import lombok.AllArgsConstructor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;
import rocks.metaldetector.support.infrastructure.CustomClientErrorHandler;
import rocks.metaldetector.support.oauth.OAuth2ClientInterceptor;

import java.util.List;

@Configuration
@AllArgsConstructor
public class SpotifyModuleConfig {

  private static final String REGISTRATION_ID = "spotify";

  private final MappingJackson2HttpMessageConverter jackson2HttpMessageConverter;
  private final StringHttpMessageConverter stringHttpMessageConverter;
  private final FormHttpMessageConverter formHttpMessageConverter;
  private final HttpComponentsClientHttpRequestFactory clientHttpRequestFactory;

  @Bean
  public RestOperations spotifyRestTemplate(RestTemplateBuilder restTemplateBuilder) {
    return restTemplate(restTemplateBuilder);
  }

  @Bean
  public RestOperations spotifyOAuthRestTemplate(RestTemplateBuilder restTemplateBuilder, SpotifyOAuth2AccessTokenSupplierWrapper oAuth2AccessTokenSupplier) {
    oAuth2AccessTokenSupplier.setRegistrationId(REGISTRATION_ID);
    RestTemplate restTemplate = restTemplate(restTemplateBuilder);
    restTemplate.getInterceptors().add(new OAuth2ClientInterceptor(oAuth2AccessTokenSupplier));
    return restTemplate;
  }

  private RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
    return restTemplateBuilder
        .requestFactory(() -> clientHttpRequestFactory)
        .errorHandler(new CustomClientErrorHandler())
        .interceptors(new SpotifyRequestInterceptor())
        .messageConverters(List.of(jackson2HttpMessageConverter, stringHttpMessageConverter, formHttpMessageConverter))
        .build();
  }
}
