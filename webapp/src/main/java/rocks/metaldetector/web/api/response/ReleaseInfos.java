package rocks.metaldetector.web.api.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReleaseInfos {

  Map<String, Long> releasesPerMonth;
  long totalReleases;
  long upcomingReleases;
  long releasesThisMonth;
  long duplicates;
}
