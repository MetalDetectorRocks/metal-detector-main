package rocks.metaldetector.butler.facade.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class ImportJobResultDto {

  private int totalCountRequested;
  private int totalCountImported;
  private LocalDateTime startTime;
  private LocalDateTime endTime;
  private long durationInSeconds;
  private boolean finished;
  private String source;

}
