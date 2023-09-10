package rocks.metaldetector.butler.client.transformer;

import org.springframework.stereotype.Component;
import rocks.metaldetector.butler.api.ButlerReleaseInfo;
import rocks.metaldetector.butler.api.ButlerStatisticsResponse;
import rocks.metaldetector.butler.facade.dto.ReleaseStatisticsDto;

@Component
public class ButlerStatisticsTransformer {

  public ReleaseStatisticsDto transform(ButlerStatisticsResponse response) {
    ButlerReleaseInfo releaseInfo = response.getReleaseInfo();
    return ReleaseStatisticsDto.builder()
        .releasesPerMonth(releaseInfo.getReleasesPerMonth())
        .totalReleases(releaseInfo.getTotalReleases())
        .upcomingReleases(releaseInfo.getUpcomingReleases())
        .releasesThisMonth(releaseInfo.getReleasesThisMonth())
        .duplicates(releaseInfo.getDuplicates())
        .build();
  }
}
