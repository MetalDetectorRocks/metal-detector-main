package rocks.metaldetector.service.summary;

import lombok.AllArgsConstructor;
import rocks.metaldetector.butler.facade.dto.ReleaseDto;
import rocks.metaldetector.web.api.response.SummaryResponse;

import java.util.List;

@AllArgsConstructor
public class SummaryServiceImpl implements SummaryService {

  public static final int RESULT_LIMIT = 4;
  public static final int TIME_RANGE_MONTHS = 6;

  private final ReleaseCollector releaseCollector;

  @Override
  public SummaryResponse createSummaryResponse() {
    List<ReleaseDto> upcomingReleases = releaseCollector.collectUpcomingReleases();
    List<ReleaseDto> recentReleases = releaseCollector.collectRecentReleases();

    return SummaryResponse.builder()
        .upcomingReleases(upcomingReleases)
        .recentReleases(recentReleases)
        .build();
  }
}
