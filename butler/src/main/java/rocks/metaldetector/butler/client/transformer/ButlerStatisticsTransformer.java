package rocks.metaldetector.butler.client.transformer;

import org.springframework.stereotype.Component;
import rocks.metaldetector.butler.api.ButlerStatisticsResponse;
import rocks.metaldetector.butler.facade.dto.ReleaseStatisticsDto;

@Component
public class ButlerStatisticsTransformer {

  public ReleaseStatisticsDto transform(ButlerStatisticsResponse response) {
    return ReleaseStatisticsDto.builder()
        .releasesPerMonth(response.getReleasesPerMonth())
        .totalReleases(response.getTotalReleases())
        .upcomingReleases(response.getUpcomingReleases())
        .releasesThisMonth(response.getReleasesThisMonth())
        .duplicates(response.getDuplicates())
        .build();
  }
}
