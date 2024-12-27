package rocks.metaldetector.testutil;

import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

public abstract class BaseSpringBootTest implements WithIntegrationTestConfig {

  @MockitoBean
  OAuth2AuthorizedClientRepository oAuth2AuthorizedClientRepository;
  @MockitoBean
  ClientRegistrationRepository clientRegistrationRepository;
}
