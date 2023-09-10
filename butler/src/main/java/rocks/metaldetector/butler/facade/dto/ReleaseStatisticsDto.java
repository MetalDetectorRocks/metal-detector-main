package rocks.metaldetector.butler.facade.dto;

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
public class ReleaseStatisticsDto {

  private Map<YearMonth, Integer> releasesPerMonth;
  private long totalReleases;
  private int upcomingReleases;
  private int releasesThisMonth;
  private int duplicates;
}
