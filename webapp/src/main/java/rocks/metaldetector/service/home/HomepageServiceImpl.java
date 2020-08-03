package rocks.metaldetector.service.home;

import lombok.AllArgsConstructor;
import rocks.metaldetector.butler.facade.ReleaseService;
import rocks.metaldetector.butler.facade.dto.ReleaseDto;
import rocks.metaldetector.service.artist.ArtistDto;
import rocks.metaldetector.service.artist.FollowArtistService;
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

    List<ReleaseDto> upcomingReleases = findUpcomingReleases(followedArtists, now);

    return HomepageResponse.builder()
        .upcomingReleases(upcomingReleases)
        .build();
  }

  private List<ReleaseDto> findUpcomingReleases(List<String> followedArtists, LocalDate now) {
    if (followedArtists.isEmpty()) {
      return Collections.emptyList();
    }

    return releaseService.findReleases(followedArtists, now, now.plusMonths(1))
        .stream().limit(RESULT_LIMIT).collect(Collectors.toList());
  }
}
