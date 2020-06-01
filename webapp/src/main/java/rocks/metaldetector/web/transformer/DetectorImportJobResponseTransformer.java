package rocks.metaldetector.web.transformer;

import org.springframework.stereotype.Service;
import rocks.metaldetector.butler.facade.dto.ImportJobResultDto;
import rocks.metaldetector.web.api.response.DetectorImportJobResponse;

@Service
public class DetectorImportJobResponseTransformer {

  public DetectorImportJobResponse transform(ImportJobResultDto importJobResultDto) {
    return DetectorImportJobResponse.builder()
        .totalCountImported(importJobResultDto.getTotalCountImported())
        .totalCountRequested(importJobResultDto.getTotalCountRequested())
        .build();
  }
}
