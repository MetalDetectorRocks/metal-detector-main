package rocks.metaldetector.butler.client.transformer;

import org.springframework.stereotype.Service;
import rocks.metaldetector.butler.api.ButlerImportJobResponse;
import rocks.metaldetector.butler.facade.dto.ImportJobResultDto;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
public class ButlerImportJobResponseTransformer {

  public ImportJobResultDto transform(ButlerImportJobResponse response) {
    return ImportJobResultDto.builder()
        .totalCountImported(response.getTotalCountImported())
        .totalCountRequested(response.getTotalCountRequested())
        .startTime(response.getStartTime())
        .endTime(response.getEndTime())
        .durationInSeconds(calculateDuration(response.getStartTime(), response.getEndTime()))
        .finished(response.getEndTime() != null)
        .build();
  }

  private long calculateDuration(LocalDateTime startTime, LocalDateTime endTime) {
    return startTime != null && endTime != null ? ChronoUnit.SECONDS.between(startTime, endTime) : 0;
  }
}
