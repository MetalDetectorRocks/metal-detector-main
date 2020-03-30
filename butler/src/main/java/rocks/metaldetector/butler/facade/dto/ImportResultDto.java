package rocks.metaldetector.butler.facade.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ImportResultDto {

  private int totalCountRequested;
  private int totalCountImported;

}
