package rocks.metaldetector.support;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Timer;

@Configuration
@EnableScheduling
@EnableAsync
public class SchedulingConfig {

  public static final String JOB_COMPLETED_THREAD_NAME = "ImportJobCompletedEvent";

  @Bean
  public Timer jobCompletedTimer() {
    return new Timer(JOB_COMPLETED_THREAD_NAME);
  }
}
