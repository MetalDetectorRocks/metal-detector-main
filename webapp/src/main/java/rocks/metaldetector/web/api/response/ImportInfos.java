package rocks.metaldetector.web.api.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImportInfos {

  String source;
  int successRate;
  LocalDate lastImport;
  LocalDate lastSuccessfulImport;
}
