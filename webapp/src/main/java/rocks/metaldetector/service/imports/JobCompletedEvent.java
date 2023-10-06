package rocks.metaldetector.service.imports;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class JobCompletedEvent extends ApplicationEvent {

  private final String jobId;

  public JobCompletedEvent(Object source, String jobId) {
    super(source);
    this.jobId = jobId;
  }
}
