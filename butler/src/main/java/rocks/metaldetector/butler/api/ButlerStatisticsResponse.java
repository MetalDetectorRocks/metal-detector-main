package rocks.metaldetector.butler.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ButlerStatisticsResponse {

  @JsonProperty("releaseInfo")
  private ButlerReleaseInfo releaseInfo;

  @JsonProperty("importInfo")
  private List<ButlerImportInfo> importInfo;
}
