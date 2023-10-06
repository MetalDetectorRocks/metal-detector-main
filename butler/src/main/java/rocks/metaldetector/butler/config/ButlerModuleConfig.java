package rocks.metaldetector.butler.config;

import lombok.AllArgsConstructor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestOperations;
import rocks.metaldetector.support.DefaultRequestLoggingInterceptor;
import rocks.metaldetector.support.PostHeaderInterceptor;
import rocks.metaldetector.support.infrastructure.CustomClientErrorHandler;
import rocks.metaldetector.support.oauth.OAuth2AccessTokenClient;

import java.util.List;

import static org.springframework.security.oauth2.core.AuthorizationGrantType.CLIENT_CREDENTIALS;

@Configuration
@AllArgsConstructor
public class ButlerModuleConfig {

  private final MappingJackson2HttpMessageConverter jackson2HttpMessageConverter;
  private final StringHttpMessageConverter stringHttpMessageConverter;
  private final HttpComponentsClientHttpRequestFactory clientHttpRequestFactory;

  @Bean
  public RestOperations releaseButlerRestOperations(RestTemplateBuilder restTemplateBuilder,
                                                    OAuth2AccessTokenClient userAccessTokenClient,
                                                    OAuth2AccessTokenClient adminAccessTokenClient) {
    userAccessTokenClient.setRegistrationId("metal-release-butler-user");
    userAccessTokenClient.setAuthorizationGrantType(CLIENT_CREDENTIALS);
    adminAccessTokenClient.setRegistrationId("metal-release-butler-admin");
    adminAccessTokenClient.setAuthorizationGrantType(CLIENT_CREDENTIALS);
    return restTemplateBuilder
        .requestFactory(() -> clientHttpRequestFactory)
        .errorHandler(new CustomClientErrorHandler())
        .interceptors(new ButlerOAuth2ClientInterceptor(userAccessTokenClient, adminAccessTokenClient), new DefaultRequestLoggingInterceptor(), new PostHeaderInterceptor())
        .messageConverters(List.of(jackson2HttpMessageConverter, stringHttpMessageConverter))
        .build();
  }
}
