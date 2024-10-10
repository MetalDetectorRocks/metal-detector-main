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
import rocks.metaldetector.support.DefaultRequestLoggingInterceptor;
import rocks.metaldetector.support.GetHeaderInterceptor;
import rocks.metaldetector.support.infrastructure.CustomClientErrorHandler;
import rocks.metaldetector.support.oauth.OAuth2AccessTokenClient;
import rocks.metaldetector.support.oauth.OAuth2ClientInterceptor;

import java.util.List;

import static org.springframework.security.oauth2.core.AuthorizationGrantType.AUTHORIZATION_CODE;
import static org.springframework.security.oauth2.core.AuthorizationGrantType.CLIENT_CREDENTIALS;

@Configuration
@AllArgsConstructor
public class SpotifyModuleConfig {

  private static final String SPOTIFY_REGISTRATION_ID_APP = "spotify-app";
  public static final String SPOTIFY_REGISTRATION_ID_USER = "spotify-user";

  private final MappingJackson2HttpMessageConverter jackson2HttpMessageConverter;
  private final StringHttpMessageConverter stringHttpMessageConverter;
  private final FormHttpMessageConverter formHttpMessageConverter;
  private final HttpComponentsClientHttpRequestFactory clientHttpRequestFactory;

  @Bean
  public RestOperations spotifyOAuthClientCredentialsRestTemplate(RestTemplateBuilder restTemplateBuilder, OAuth2AccessTokenClient accessTokenClient) {
    accessTokenClient.setRegistrationId(SPOTIFY_REGISTRATION_ID_APP);
    accessTokenClient.setAuthorizationGrantType(CLIENT_CREDENTIALS);
    RestTemplate restTemplate = restTemplate(restTemplateBuilder);
    restTemplate.getInterceptors().add(new OAuth2ClientInterceptor(accessTokenClient));
    return restTemplate;
  }

  @Bean
  public RestOperations spotifyOAuthAuthorizationCodeRestTemplate(RestTemplateBuilder restTemplateBuilder, OAuth2AccessTokenClient accessTokenClient) {
    accessTokenClient.setRegistrationId(SPOTIFY_REGISTRATION_ID_USER);
    accessTokenClient.setAuthorizationGrantType(AUTHORIZATION_CODE);
    RestTemplate restTemplate = restTemplate(restTemplateBuilder);
    restTemplate.getInterceptors().add(new OAuth2ClientInterceptor(accessTokenClient));
    return restTemplate;
  }

  private RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
    return restTemplateBuilder
        .requestFactory(() -> clientHttpRequestFactory)
        .errorHandler(new CustomClientErrorHandler())
        .interceptors(new DefaultRequestLoggingInterceptor(), new GetHeaderInterceptor())
        .messageConverters(List.of(jackson2HttpMessageConverter, stringHttpMessageConverter, formHttpMessageConverter))
        .build();
  }
}
