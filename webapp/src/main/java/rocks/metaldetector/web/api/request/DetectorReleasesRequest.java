package rocks.metaldetector.web.api.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DetectorReleasesRequest {

  @Nullable
  private LocalDate dateFrom;

  @Nullable
  private LocalDate dateTo;

  @NotNull
  private List<String> artists;

}
