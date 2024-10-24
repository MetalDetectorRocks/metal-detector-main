package rocks.metaldetector.web.api.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

import static jakarta.validation.constraints.Pattern.Flag.CASE_INSENSITIVE;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaginatedReleasesRequest implements WithTimeRangeValidation {

  @Min(value = 1, message = "'page' must be greater than zero!")
  @Builder.Default
  private int page = 1;

  @Min(value = 1, message = "'size' must be greater than zero!")
  @Max(value = 50, message = "'size' must be equal or less than 50!")
  @Builder.Default
  private int size = 40;

  @NotBlank
  private String sort;

  @NotNull
  @Pattern(regexp = "asc|desc", flags = CASE_INSENSITIVE)
  @Builder.Default
  private String direction = "asc";

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private LocalDate dateFrom;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private LocalDate dateTo;

  private String query;

  @NotNull
  @Pattern(regexp = "all|my", flags = CASE_INSENSITIVE)
  @Builder.Default
  private String releasesFilter = "all";
}
