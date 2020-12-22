package rocks.metaldetector.butler.facade.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImportJobResultDto {

  private int totalCountRequested;
  private int totalCountImported;
  private LocalDateTime startTime;
  private LocalDateTime endTime;
  private long durationInSeconds;
  private String state;
  private String source;

}
