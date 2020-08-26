package rocks.metaldetector.service.summary;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import rocks.metaldetector.butler.facade.ReleaseService;
import rocks.metaldetector.butler.facade.dto.ReleaseDto;
import rocks.metaldetector.service.artist.ArtistDto;
import rocks.metaldetector.service.artist.FollowArtistService;
import rocks.metaldetector.support.PageRequest;
import rocks.metaldetector.support.TimeRange;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static rocks.metaldetector.service.summary.SummaryServiceImpl.RESULT_LIMIT;
import static rocks.metaldetector.service.summary.SummaryServiceImpl.TIME_RANGE_MONTHS;

@Service
@AllArgsConstructor
public class ReleaseCollector {

  private final ReleaseService releaseService;
  private final FollowArtistService followArtistService;

  public List<ReleaseDto> collectUpcomingReleases() {
    LocalDate now = LocalDate.now();
    TimeRange timeRange = new TimeRange(now, now.plusMonths(TIME_RANGE_MONTHS));

    return collectReleases(timeRange);
  }

  public List<ReleaseDto> collectRecentReleases() {
    LocalDate now = LocalDate.now();
    TimeRange timeRange = new TimeRange(now.minusMonths(TIME_RANGE_MONTHS), now);

    return collectReleases(timeRange);
  }

  private List<ReleaseDto> collectReleases(TimeRange timeRange) {
    List<String> followedArtists = followArtistService.getFollowedArtistsOfCurrentUser()
        .stream().map(ArtistDto::getArtistName).collect(Collectors.toList());

    if (followedArtists.isEmpty()) {
      return Collections.emptyList();
    }

    PageRequest pageRequest = new PageRequest(1, RESULT_LIMIT);
    return releaseService.findReleases(followedArtists, timeRange, pageRequest);
  }
}
