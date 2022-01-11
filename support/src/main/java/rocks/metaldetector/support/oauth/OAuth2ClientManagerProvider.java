package rocks.metaldetector.support.oauth;

import lombok.AllArgsConstructor;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.stereotype.Component;

import static java.lang.Thread.currentThread;
import static rocks.metaldetector.support.infrastructure.ApacheHttpClientConfig.SCHEDULED_TASK_NAME_PREFIX;

@Component
@AllArgsConstructor
public class OAuth2ClientManagerProvider {

  private final OAuth2AuthorizedClientManager authorizedClientManager;
  private final OAuth2AuthorizedClientManager schedulingAuthorizedClientManager;

  public OAuth2AuthorizedClientManager provide() {
    if (currentThread().getName().startsWith(SCHEDULED_TASK_NAME_PREFIX)) {
      return schedulingAuthorizedClientManager;
    }
    return authorizedClientManager;
  }
}
