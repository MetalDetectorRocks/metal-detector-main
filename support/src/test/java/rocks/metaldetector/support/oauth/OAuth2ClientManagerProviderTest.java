package rocks.metaldetector.support.oauth;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;

import static org.mockito.Mockito.reset;
import static rocks.metaldetector.support.infrastructure.ApacheHttpClientConfig.SCHEDULED_TASK_NAME_PREFIX;

@ExtendWith(MockitoExtension.class)
class OAuth2ClientManagerProviderTest implements WithAssertions {

  @Mock
  private OAuth2AuthorizedClientManager authorizedClientManager;

  @Mock
  private OAuth2AuthorizedClientManager schedulingAuthorizedClientManager;

  private OAuth2ClientManagerProvider underTest;

  @BeforeEach
  void setup() {
    underTest = new OAuth2ClientManagerProvider(authorizedClientManager, schedulingAuthorizedClientManager);
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
  @DisplayName("For a scheduled thread the default authorizedClientManager is returned")
  void test_scheduling_manager_returned() {
    // given
    var scheduler = new ThreadPoolTaskScheduler();
    scheduler.setThreadNamePrefix(SCHEDULED_TASK_NAME_PREFIX);
    scheduler.initialize();

    scheduler.execute(() -> {
      // when
      var result = underTest.provide();

      // then
      assertThat(result).isEqualTo(authorizedClientManager);
    });
  }
}
