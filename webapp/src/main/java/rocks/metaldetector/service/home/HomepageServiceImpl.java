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

    List<ReleaseDto> upcomingReleases = findUpcomingReleases(followedArtists);
    return HomepageResponse.builder()
        .upcomingReleases(upcomingReleases)
        .build();
  }

  private List<ReleaseDto> findUpcomingReleases(List<String> followedArtists) {
    if (followedArtists.isEmpty()) {
      return Collections.emptyList();
    }

    LocalDate now = LocalDate.now();
    TimeRange timeRange = new TimeRange(now, now.plusMonths(6));
    PageRequest pageRequest = new PageRequest(1, RESULT_LIMIT);
    return releaseService.findReleases(followedArtists, timeRange, pageRequest);
  }
}
