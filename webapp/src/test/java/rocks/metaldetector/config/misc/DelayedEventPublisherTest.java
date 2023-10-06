package rocks.metaldetector.config.misc;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import rocks.metaldetector.service.imports.JobCompletedEvent;

import java.util.Timer;
import java.util.TimerTask;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static rocks.metaldetector.config.misc.DelayedEventPublisher.DELAY_IN_MILLISECONDS;

@ExtendWith(MockitoExtension.class)
class DelayedEventPublisherTest implements WithAssertions {

  @Mock
  private ApplicationEventPublisher applicationEventPublisher;

  @Mock
  private Timer timer;

  private DelayedEventPublisher underTest;

  @Captor
  private ArgumentCaptor<TimerTask> argumentCaptorTimerTask;

  @Captor
  private ArgumentCaptor<JobCompletedEvent> argumentCaptorJobEvent;

  @BeforeEach
  void setup() {
    underTest = new DelayedEventPublisher(timer);
    underTest.setApplicationEventPublisher(applicationEventPublisher);
  }

  @AfterEach
  void tearDown() {
    reset(applicationEventPublisher, timer);
  }

  @Test
  @DisplayName("A jobCreated event with the given id is scheduled")
  void test_event_published() {
    // given
    var jobId = "666";

    // when
    underTest.publishDelayedJobEvent(jobId);

    // then
    verify(timer).schedule(argumentCaptorTimerTask.capture(), anyLong());

    TimerTask timerTask = argumentCaptorTimerTask.getValue();
    timerTask.run();

    verify(applicationEventPublisher).publishEvent(argumentCaptorJobEvent.capture());

    JobCompletedEvent event = argumentCaptorJobEvent.getValue();
    assertThat(event.getJobId()).isEqualTo(jobId);
  }

  @Test
  @DisplayName("A jobCreated event is scheduled after given delay")
  void test_event_delayed() {
    // when
    underTest.publishDelayedJobEvent("666");

    // then
    verify(timer).schedule(any(), eq(DELAY_IN_MILLISECONDS));
  }
}
