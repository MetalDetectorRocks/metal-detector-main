package rocks.metaldetector.butler.client.transformer;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import rocks.metaldetector.butler.api.ButlerImportJob;
import rocks.metaldetector.butler.facade.dto.ImportJobResultDto;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
@AllArgsConstructor
public class ButlerImportJobTransformer {

  public ImportJobResultDto transform(ButlerImportJob response) {
    return ImportJobResultDto.builder()
        .totalCountImported(response.getTotalCountImported())
        .totalCountRequested(response.getTotalCountRequested())
        .startTime(response.getStartTime())
        .endTime(response.getEndTime())
        .durationInSeconds(calculateDuration(response.getStartTime(), response.getEndTime()))
        .state(response.getState())
        .source(response.getSource())
        .build();
  }

  private long calculateDuration(LocalDateTime startTime, LocalDateTime endTime) {
    return startTime != null && endTime != null ? ChronoUnit.SECONDS.between(startTime, endTime) : 0;
  }
}
