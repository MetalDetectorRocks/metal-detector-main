package rocks.metaldetector.butler.client.transformer;

import org.springframework.stereotype.Service;
import rocks.metaldetector.butler.api.ButlerImportJobResponse;
import rocks.metaldetector.butler.facade.dto.ImportJobResultDto;

@Service
public class ButlerImportJobResponseTransformer {

  public ImportJobResultDto transform(ButlerImportJobResponse response) {
    return ImportJobResultDto.builder()
        .totalCountImported(response.getTotalCountImported())
        .totalCountRequested(response.getTotalCountRequested())
        .build();
  }
}
