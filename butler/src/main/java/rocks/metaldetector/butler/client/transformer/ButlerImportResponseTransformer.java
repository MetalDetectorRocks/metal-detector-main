package rocks.metaldetector.butler.client.transformer;

import org.springframework.stereotype.Service;
import rocks.metaldetector.butler.api.ButlerImportResponse;
import rocks.metaldetector.butler.facade.dto.ImportResultDto;

@Service
public class ButlerImportResponseTransformer {

  public ImportResultDto transform(ButlerImportResponse response) {
    return ImportResultDto.builder()
        .totalCountImported(response.getTotalCountImported())
        .totalCountRequested(response.getTotalCountRequested())
        .build();
  }
}
