package rocks.metaldetector.web.api.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaginatedReleasesRequest implements WithTimeRangeValidation {

  @Min(value = 1L, message = "'page' must be greater than zero!")
  private int page;

  @Min(value = 1L, message = "'size' must be greater than zero!")
  @Max(value = 50L, message = "'size' must be equal or less than 50!")
  private int size;

  @Nullable
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private LocalDate dateFrom;

  @Nullable
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private LocalDate dateTo;

  @NotNull
  private List<String> artists;

}
