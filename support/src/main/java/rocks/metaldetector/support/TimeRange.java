package rocks.metaldetector.support;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimeRange {

  private LocalDate dateFrom;
  private LocalDate dateTo;

}
