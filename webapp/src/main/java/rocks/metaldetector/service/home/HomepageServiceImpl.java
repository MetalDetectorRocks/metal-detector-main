package rocks.metaldetector.service.home;

import lombok.AllArgsConstructor;
import rocks.metaldetector.butler.facade.ReleaseService;
import rocks.metaldetector.butler.facade.dto.ReleaseDto;
import rocks.metaldetector.service.artist.ArtistDto;
import rocks.metaldetector.service.artist.FollowArtistService;
import rocks.metaldetector.support.PageRequest;
import rocks.metaldetector.support.TimeRange;
import rocks.metaldetector.web.api.response.HomepageResponse;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
public class HomepageServiceImpl implements HomepageService {

  static final int RESULT_LIMIT = 4;

  private final ReleaseService releaseService;
  private final FollowArtistService followArtistService;

  @Override
  public HomepageResponse createHomeResponse() {
    List<String> followedArtists = followArtistService.getFollowedArtistsOfCurrentUser()
        .stream().map(ArtistDto::getArtistName).collect(Collectors.toList());

    LocalDate now = LocalDate.now();
    PageRequest pageRequest = new PageRequest(1, RESULT_LIMIT);

    List<ReleaseDto> upcomingReleases = findUpcomingReleases(followedArtists, now, pageRequest);
    List<ReleaseDto> recentReleases = findRecentReleases(followedArtists, now, pageRequest);

    return HomepageResponse.builder()
        .upcomingReleases(upcomingReleases)
        .recentReleases(recentReleases)
        .build();
  }

  private List<ReleaseDto> findUpcomingReleases(List<String> followedArtists, LocalDate now, PageRequest pageRequest) {
    if (followedArtists.isEmpty()) {
      return Collections.emptyList();
    }

    TimeRange timeRange = new TimeRange(now, now.plusMonths(6));
    return releaseService.findReleases(followedArtists, timeRange, pageRequest);
  }

  private List<ReleaseDto> findRecentReleases(List<String> followedArtists, LocalDate now, PageRequest pageRequest) {
    if (followedArtists.isEmpty()) {
      return Collections.emptyList();
    }

    TimeRange timeRange = new TimeRange(now.minusMonths(6), now);
    return releaseService.findReleases(followedArtists, timeRange, pageRequest);
  }
}
