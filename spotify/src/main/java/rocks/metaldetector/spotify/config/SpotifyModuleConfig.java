package rocks.metaldetector.spotify.config;

import lombok.AllArgsConstructor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.web.client.RestOperations;
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
    return restTemplateBuilder
        .requestFactory(() -> clientHttpRequestFactory)
        .errorHandler(new CustomClientErrorHandler())
        .interceptors(new SpotifyRequestInterceptor())
        .messageConverters(List.of(jackson2HttpMessageConverter, stringHttpMessageConverter, formHttpMessageConverter))
        .build();
  }

  @Bean
  public RestOperations spotifyOAuthRestTemplate(RestTemplateBuilder restTemplateBuilder, OAuth2AuthorizedClientManager authorizedClientManager) {
    return restTemplateBuilder
        .requestFactory(() -> clientHttpRequestFactory)
        .errorHandler(new CustomClientErrorHandler())
        .interceptors(new SpotifyRequestInterceptor(), new OAuth2ClientInterceptor(authorizedClientManager, REGISTRATION_ID))
        .messageConverters(List.of(jackson2HttpMessageConverter, stringHttpMessageConverter, formHttpMessageConverter))
        .build();
  }
}
