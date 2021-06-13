package rocks.metaldetector.support.oauth;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

@Configuration
public class OAuth2ClientManagerConfig {

  @Bean
  public OAuth2AuthorizedClientManager authorizedClientManager(OAuth2AuthorizedClientService oAuth2AuthorizedClientService,
                                                               ClientRegistrationRepository clients) {
    OAuth2AuthorizedClientProvider authorizedClientProvider = OAuth2AuthorizedClientProviderBuilder.builder()
        .clientCredentials()
        .build();
    var manager = new AuthorizedClientServiceOAuth2AuthorizedClientManager(clients, oAuth2AuthorizedClientService);
    manager.setAuthorizedClientProvider(authorizedClientProvider);
    return manager;
  }
}
