package rocks.metaldetector.butler.facade.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ImportInfoDto {

  private String source;
  private int successRate;
  private LocalDateTime lastImport;
  private LocalDateTime lastSuccessfulImport;
}
