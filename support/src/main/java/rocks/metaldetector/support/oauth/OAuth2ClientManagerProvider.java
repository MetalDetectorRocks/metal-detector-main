package rocks.metaldetector.support.oauth;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.task.TaskSchedulingProperties;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.stereotype.Component;

import static java.lang.Thread.currentThread;

@Component
public class OAuth2ClientManagerProvider {

  private final OAuth2AuthorizedClientManager authorizedClientManager;
  private final OAuth2AuthorizedClientManager schedulingAuthorizedClientManager;
  private final TaskSchedulingProperties taskSchedulingProperties;

  public OAuth2ClientManagerProvider(@Qualifier("authorizedClientManager") OAuth2AuthorizedClientManager authorizedClientManager,
                                     @Qualifier("schedulingAuthorizedClientManager") OAuth2AuthorizedClientManager schedulingAuthorizedClientManager,
                                     TaskSchedulingProperties taskSchedulingProperties) {
    this.authorizedClientManager = authorizedClientManager;
    this.schedulingAuthorizedClientManager = schedulingAuthorizedClientManager;
    this.taskSchedulingProperties = taskSchedulingProperties;
  }

  public OAuth2AuthorizedClientManager provide() {
    if (currentThread().getName().startsWith(taskSchedulingProperties.getThreadNamePrefix())) {
      return schedulingAuthorizedClientManager;
    }
    return authorizedClientManager;
  }
}
