package rocks.metaldetector.config.misc;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Timer;

import static rocks.metaldetector.support.oauth.OAuth2ClientManagerProvider.JOB_COMPLETED_THREAD_NAME;

@Configuration
@EnableScheduling
@EnableAsync
public class SchedulingConfig {

  @Bean
  public Timer jobCompletedTimer() {
    return new Timer(JOB_COMPLETED_THREAD_NAME);
  }
}
