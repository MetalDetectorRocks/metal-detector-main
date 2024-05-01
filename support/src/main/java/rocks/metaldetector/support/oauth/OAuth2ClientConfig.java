package rocks.metaldetector.support.oauth;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.security.crypto.keygen.Base64StringKeyGenerator;
import org.springframework.security.crypto.keygen.StringKeyGenerator;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.JdbcOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;

import java.util.Base64;

@Configuration
public class OAuth2ClientConfig {

  public static final String OAUTH_AUTHORIZATION_ENDPOINT = "/oauth2/authorization";

  @Bean
  public OAuth2AuthorizedClientManager authorizedClientManager(OAuth2AuthorizedClientRepository authorizedClientRepository,
                                                               ClientRegistrationRepository clientRegistrationRepository) {
    OAuth2AuthorizedClientProvider authorizedClientProvider = OAuth2AuthorizedClientProviderBuilder.builder()
        .clientCredentials()
        .authorizationCode()
        .refreshToken()
        .build();
    var manager = new DefaultOAuth2AuthorizedClientManager(clientRegistrationRepository, authorizedClientRepository);
    manager.setAuthorizedClientProvider(authorizedClientProvider);
    return manager;
  }

  @Bean
  public OAuth2AuthorizedClientManager schedulingAuthorizedClientManager(OAuth2AuthorizedClientService authorizedClientService,
                                                                         ClientRegistrationRepository clientRegistrationRepository) {
    OAuth2AuthorizedClientProvider authorizedClientProvider = OAuth2AuthorizedClientProviderBuilder.builder()
        .clientCredentials()
        .build();
    var manager = new AuthorizedClientServiceOAuth2AuthorizedClientManager(clientRegistrationRepository, authorizedClientService);
    manager.setAuthorizedClientProvider(authorizedClientProvider);
    return manager;
  }

  @Bean
  public OAuth2AuthorizedClientService oAuth2AuthorizedClientService(JdbcOperations jdbcOperations,
                                                                     ClientRegistrationRepository clients) {
    return new JdbcOAuth2AuthorizedClientService(jdbcOperations, clients);
  }

  @Bean
  public OAuth2AuthorizationRequestResolver oAuth2AuthorizationRequestResolver(ClientRegistrationRepository clientRegistrationRepository, OAuth2AuthorizationCodeStateGenerator stateGenerator) {
    DefaultOAuth2AuthorizationRequestResolver resolver = new DefaultOAuth2AuthorizationRequestResolver(clientRegistrationRepository, OAUTH_AUTHORIZATION_ENDPOINT);
    resolver.setAuthorizationRequestCustomizer(customizer -> customizer
        .additionalParameters(params -> {
          params.put("prompt", "consent");
          params.put("access_type", "offline");
        })
        .state(stateGenerator.generateState()));
    return resolver;
  }

  @Bean
  public StringKeyGenerator stringKeyGenerator() {
    return new Base64StringKeyGenerator(Base64.getUrlEncoder());
  }
}
