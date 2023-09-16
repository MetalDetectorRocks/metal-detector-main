package rocks.metaldetector.butler.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.YearMonth;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ButlerReleaseInfo {

  @JsonProperty("releasesPerMonth")
  private Map<YearMonth, Integer> releasesPerMonth;

  @JsonProperty("totalReleases")
  private long totalReleases;

  @JsonProperty("upcomingReleases")
  private int upcomingReleases;

  @JsonProperty("releasesThisMonth")
  private int releasesThisMonth;

  @JsonProperty("duplicates")
  private int duplicates;
}
