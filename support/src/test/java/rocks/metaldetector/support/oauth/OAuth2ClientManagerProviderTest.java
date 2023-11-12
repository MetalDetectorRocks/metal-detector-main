package rocks.metaldetector.support.oauth;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.autoconfigure.task.TaskSchedulingProperties;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;

import static java.lang.Thread.currentThread;
import static org.mockito.Mockito.reset;
import static rocks.metaldetector.support.SchedulingConfig.JOB_COMPLETED_THREAD_NAME;

@ExtendWith(MockitoExtension.class)
class OAuth2ClientManagerProviderTest implements WithAssertions {

  @Mock
  private OAuth2AuthorizedClientManager authorizedClientManager;

  @Mock
  private OAuth2AuthorizedClientManager schedulingAuthorizedClientManager;

  private TaskSchedulingProperties taskSchedulingProperties;

  private OAuth2ClientManagerProvider underTest;

  @BeforeEach
  void setup() {
    taskSchedulingProperties = new TaskSchedulingProperties();
    taskSchedulingProperties.setThreadNamePrefix("scheduling-");
    underTest = new OAuth2ClientManagerProvider(authorizedClientManager, schedulingAuthorizedClientManager, taskSchedulingProperties);
  }

  @AfterEach
  void tearDown() {
    reset(authorizedClientManager, schedulingAuthorizedClientManager);
  }

  @Test
  @DisplayName("For a normal thread the default authorizedClientManager is returned")
  void test_default_manager_returned() {
    // when
    var result = underTest.provide();

    // then
    assertThat(result).isEqualTo(authorizedClientManager);
  }

  @Test
  @DisplayName("For a scheduled thread the schedulingAuthorizedClientManager is returned")
  void test_scheduling_manager_returned_for_scheduled() {
    // given
    var currentThreadName = currentThread().getName();
    try {
      currentThread().setName(taskSchedulingProperties.getThreadNamePrefix());

      // when
      var result = underTest.provide();

      // then
      assertThat(result).isEqualTo(schedulingAuthorizedClientManager);
    } finally {
      currentThread().setName(currentThreadName);
    }
  }

  @Test
  @DisplayName("For a timed thread the schedulingAuthorizedClientManager is returned")
  void test_scheduling_manager_returned_for_timed() {
    // given
    var currentThreadName = currentThread().getName();
    try {
      currentThread().setName(JOB_COMPLETED_THREAD_NAME);

      // when
      var result = underTest.provide();

      // then
      assertThat(result).isEqualTo(schedulingAuthorizedClientManager);
    } finally {
      currentThread().setName(currentThreadName);
    }
  }
}
