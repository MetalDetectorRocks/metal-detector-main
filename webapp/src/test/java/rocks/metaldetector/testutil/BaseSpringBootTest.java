package rocks.metaldetector.testutil;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;

@SpringBootTest
public abstract class BaseSpringBootTest implements WithIntegrationTestConfig {

  @MockBean
  OAuth2AuthorizedClientRepository oAuth2AuthorizedClientRepository;
  @MockBean
  ClientRegistrationRepository clientRegistrationRepository;
}
