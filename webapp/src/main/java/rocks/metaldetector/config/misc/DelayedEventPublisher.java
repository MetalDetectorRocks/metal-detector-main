package rocks.metaldetector.config.misc;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Component;
import rocks.metaldetector.service.imports.JobCompletedEvent;

import java.time.Duration;
import java.util.Timer;
import java.util.TimerTask;

import static java.time.temporal.ChronoUnit.HOURS;

@Component
@RequiredArgsConstructor
public class DelayedEventPublisher implements ApplicationEventPublisherAware {

  protected static final Duration DELAY = Duration.of(1, HOURS);

  private final Timer jobCompletedTimer;
  private ApplicationEventPublisher applicationEventPublisher;

  @Override
  public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
    this.applicationEventPublisher = applicationEventPublisher;
  }

  public void publishDelayedJobEvent(String jobId) {
    JobCompletedEvent event = new JobCompletedEvent(jobId);
    TimerTask task = new TimerTask() {
      @Override
      public void run() {
        applicationEventPublisher.publishEvent(event);
      }
    };
    jobCompletedTimer.schedule(task, DELAY.toMillis());
  }
}
