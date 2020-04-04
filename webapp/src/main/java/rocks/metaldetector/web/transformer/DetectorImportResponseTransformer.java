package rocks.metaldetector.web.transformer;

import org.springframework.stereotype.Service;
import rocks.metaldetector.butler.facade.dto.ImportResultDto;
import rocks.metaldetector.web.api.response.DetectorImportResponse;

@Service
public class DetectorImportResponseTransformer {

  public DetectorImportResponse transform(ImportResultDto importResultDto) {
    return DetectorImportResponse.builder()
        .totalCountImported(importResultDto.getTotalCountImported())
        .totalCountRequested(importResultDto.getTotalCountRequested())
        .build();
  }
}
