package rocks.metaldetector.config.misc;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Component;
import rocks.metaldetector.service.imports.JobCompletedEvent;

import java.util.Timer;
import java.util.TimerTask;

@Component
@RequiredArgsConstructor
public class DelayedEventPublisher implements ApplicationEventPublisherAware {

  protected static final long DELAY_IN_MILLISECONDS = 15 * 60 * 1000;

  private final Timer jobCompletedTimer;
  private ApplicationEventPublisher applicationEventPublisher;

  @Override
  public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
    this.applicationEventPublisher = applicationEventPublisher;
  }

  public void publishDelayedJobEvent(String jobId) {
    JobCompletedEvent event = new JobCompletedEvent(this, jobId);
    TimerTask task = new TimerTask() {
      @Override
      public void run() {
        applicationEventPublisher.publishEvent(event);
      }
    };
    jobCompletedTimer.schedule(task, DELAY_IN_MILLISECONDS);
  }
}
