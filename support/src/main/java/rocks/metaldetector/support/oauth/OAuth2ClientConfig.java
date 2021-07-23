package rocks.metaldetector.support.oauth;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.security.oauth2.client.JdbcOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;

@Configuration
public class OAuth2ClientConfig {

  @Bean
  public OAuth2AuthorizedClientManager authorizedClientManager(OAuth2AuthorizedClientRepository authorizedClientRepository,
                                                               ClientRegistrationRepository clientRegistrationRepository) {
    OAuth2AuthorizedClientProvider authorizedClientProvider = OAuth2AuthorizedClientProviderBuilder.builder()
        .clientCredentials()
        .authorizationCode()
        .build();
    var manager = new DefaultOAuth2AuthorizedClientManager(clientRegistrationRepository, authorizedClientRepository);
    manager.setAuthorizedClientProvider(authorizedClientProvider);
    return manager;
  }

  @Bean
  public OAuth2AuthorizedClientService oAuth2AuthorizedClientService(JdbcOperations jdbcOperations,
                                                                     ClientRegistrationRepository clients) {
    return new JdbcOAuth2AuthorizedClientService(jdbcOperations, clients);
  }
}
