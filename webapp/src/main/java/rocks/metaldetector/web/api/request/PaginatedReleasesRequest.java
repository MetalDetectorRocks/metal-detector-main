package rocks.metaldetector.web.api.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaginatedReleasesRequest implements WithTimeRangeValidation {

  @Min(value = 1, message = "'page' must be greater than zero!")
  private int page = 1;

  @Min(value = 1, message = "'size' must be greater than zero!")
  @Max(value = 50, message = "'size' must be equal or less than 50!")
  private int size = 40;

  @Nullable
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private LocalDate dateFrom;

  @Nullable
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private LocalDate dateTo;

}
