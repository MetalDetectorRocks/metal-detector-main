package rocks.metaldetector.butler.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ButlerStatisticsResponse {

  @JsonProperty("releaseInfo")
  private ButlerReleaseInfo releaseInfo;
}
