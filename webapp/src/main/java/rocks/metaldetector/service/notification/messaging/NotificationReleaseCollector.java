package rocks.metaldetector.service.notification.messaging;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.stereotype.Component;
import rocks.metaldetector.butler.facade.ReleaseService;
import rocks.metaldetector.butler.facade.dto.ReleaseDto;
import rocks.metaldetector.persistence.domain.user.AbstractUserEntity;
import rocks.metaldetector.service.artist.ArtistDto;
import rocks.metaldetector.service.artist.FollowArtistService;
import rocks.metaldetector.support.TimeRange;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class NotificationReleaseCollector {

  private static final String RELEASE_STATE_OK = "OK";

  private final ReleaseService releaseService;
  private final FollowArtistService followArtistService;

  public NotificationReleaseCollector.ReleaseContainer fetchReleasesForUserAndFrequency(AbstractUserEntity user, int frequency, boolean notifyReissues) {
    List<String> followedArtistNames = getFollowedArtistNames(user);

    List<ReleaseDto> upcomingReleases = new ArrayList<>();
    List<ReleaseDto> recentReleases = new ArrayList<>();

    if (!followedArtistNames.isEmpty()) {
      var now = LocalDate.now();
      upcomingReleases = releaseService.findAllReleases(followedArtistNames, new TimeRange(now, now.plusWeeks(frequency))).stream()
          .filter(release -> release.getState().equals(RELEASE_STATE_OK))
          .filter(release -> !release.isReissue() || notifyReissues)
          .collect(Collectors.toList());
      recentReleases = releaseService.findAllReleases(followedArtistNames, new TimeRange(now.minusWeeks(frequency), now.minusDays(1))).stream()
          .filter(release -> release.getState().equals(RELEASE_STATE_OK))
          .filter(release -> !release.isReissue() || notifyReissues)
          .collect(Collectors.toList());
    }
    return new ReleaseContainer(upcomingReleases, recentReleases);
  }

  public List<ReleaseDto> fetchTodaysReleaseForUser(AbstractUserEntity user, boolean notifyReissues) {
    List<String> followedArtistNames = getFollowedArtistNames(user);

    if (!followedArtistNames.isEmpty()) {
      var now = LocalDate.now();
      return releaseService.findAllReleases(followedArtistNames, new TimeRange(now, now)).stream()
          .filter(release -> release.getState().equals(RELEASE_STATE_OK))
          .filter(release -> !release.isReissue() || notifyReissues)
          .collect(Collectors.toList());
    }
    return Collections.emptyList();
  }

  public List<ReleaseDto> fetchTodaysAnnouncementsForUser(AbstractUserEntity user, boolean notifyReissues) {
    List<String> followedArtistNames = getFollowedArtistNames(user);

    if (!followedArtistNames.isEmpty()) {
      var now = LocalDate.now();
      return releaseService.findAllReleases(followedArtistNames, new TimeRange(now, null)).stream()
          .filter(release -> release.getAnnouncementDate().equals(now))
          .filter(release -> release.getState().equals(RELEASE_STATE_OK))
          .filter(release -> !release.isReissue() || notifyReissues)
          .collect(Collectors.toList());
    }
    return Collections.emptyList();
  }

  private List<String> getFollowedArtistNames(AbstractUserEntity user) {
    return followArtistService.getFollowedArtistsOfUser(user).stream()
        .map(ArtistDto::getArtistName)
        .collect(Collectors.toList());
  }

  @AllArgsConstructor
  @Getter
  @EqualsAndHashCode
  public static class ReleaseContainer {

    private final List<ReleaseDto> upcomingReleases;
    private final List<ReleaseDto> recentReleases;
  }
}
