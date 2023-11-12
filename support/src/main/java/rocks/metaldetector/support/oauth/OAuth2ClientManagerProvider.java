package rocks.metaldetector.support.oauth;

import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.task.TaskSchedulingProperties;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.stereotype.Component;

import static java.lang.Thread.currentThread;
import static rocks.metaldetector.support.SchedulingConfig.JOB_COMPLETED_THREAD_NAME;

@Component
@AllArgsConstructor
public class OAuth2ClientManagerProvider {

  private final OAuth2AuthorizedClientManager authorizedClientManager;
  private final OAuth2AuthorizedClientManager schedulingAuthorizedClientManager;
  private final TaskSchedulingProperties taskSchedulingProperties;

  public OAuth2AuthorizedClientManager provide() {
    if (currentThread().getName().startsWith(taskSchedulingProperties.getThreadNamePrefix()) ||
        currentThread().getName().startsWith(JOB_COMPLETED_THREAD_NAME)) {
      return schedulingAuthorizedClientManager;
    }
    return authorizedClientManager;
  }
}
