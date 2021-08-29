package rocks.metaldetector.service.notification.messaging;

import org.assertj.core.api.WithAssertions;
import org.assertj.core.data.TemporalUnitLessThanOffset;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import rocks.metaldetector.butler.facade.ReleaseService;
import rocks.metaldetector.butler.facade.dto.ReleaseDto;
import rocks.metaldetector.persistence.domain.user.AbstractUserEntity;
import rocks.metaldetector.service.artist.FollowArtistService;
import rocks.metaldetector.service.user.UserEntityFactory;
import rocks.metaldetector.support.TimeRange;
import rocks.metaldetector.testutil.DtoFactory.ArtistDtoFactory;
import rocks.metaldetector.testutil.DtoFactory.ReleaseDtoFactory;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static java.time.temporal.ChronoUnit.DAYS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class NotificationReleaseCollectorTest implements WithAssertions {

  private static final AbstractUserEntity USER = UserEntityFactory.createUser("user", "user@user.user");

  @Mock
  private ReleaseService releaseService;

  @Mock
  private FollowArtistService followArtistService;

  @InjectMocks
  private NotificationReleaseCollector underTest;

  @AfterEach
  void tearDown() {
    reset(releaseService, followArtistService);
  }

  @Nested
  @DisplayName("Tests for frequency releases")
  class FrequencyReleases {

    @Test
    @DisplayName("followArtistService is called")
    void test_follow_artist_service_called() {
      // when
      underTest.fetchReleasesForUserAndFrequency(USER, 666, false);

      // then
      verify(followArtistService).getFollowedArtistsOfUser(USER);
    }

    @Test
    @DisplayName("releaseService is called for user's current and recent releases")
    void test_release_service_called_for_current_releases() {
      // given
      ArgumentCaptor<TimeRange> argumentCaptor = ArgumentCaptor.forClass(TimeRange.class);
      TemporalUnitLessThanOffset offset = new TemporalUnitLessThanOffset(1, DAYS);
      var now = LocalDate.now();
      var frequency = 6;
      var followedArtist = ArtistDtoFactory.createDefault();
      var timeRangeUpcoming = new TimeRange(now, now.plusWeeks(frequency));
      var timeRangeRecent = new TimeRange(now.minusWeeks(frequency), now.minusDays(1));
      doReturn(List.of(followedArtist)).when(followArtistService).getFollowedArtistsOfUser(any());
      doReturn(List.of(ReleaseDtoFactory.createDefault())).when(releaseService).findAllReleases(anyList(), any());

      // when
      underTest.fetchReleasesForUserAndFrequency(USER, frequency, false);

      // then
      verify(releaseService, times(2)).findAllReleases(eq(List.of(followedArtist.getArtistName())), argumentCaptor.capture());
      var timeRanges = argumentCaptor.getAllValues();
      assertThat(timeRanges.get(0).getDateFrom()).isCloseTo(timeRangeUpcoming.getDateFrom(), offset);
      assertThat(timeRanges.get(0).getDateTo()).isCloseTo(timeRangeUpcoming.getDateTo(), offset);
      assertThat(timeRanges.get(1).getDateFrom()).isCloseTo(timeRangeRecent.getDateFrom(), offset);
      assertThat(timeRanges.get(1).getDateTo()).isCloseTo(timeRangeRecent.getDateTo(), offset);
    }

    @Test
    @DisplayName("releaseService is not called if not followed artists exist")
    void test_release_service_not_called() {
      // given
      doReturn(Collections.emptyList()).when(followArtistService).getFollowedArtistsOfUser(any());

      // when
      underTest.fetchReleasesForUserAndFrequency(USER, 666, false);

      // then
      verifyNoInteractions(releaseService);
    }

    @Test
    @DisplayName("releases are returned in container")
    void test_releases_returned() {
      // given
      var releases = List.of(ReleaseDtoFactory.createDefault());
      var expectedReleaseContainer = new NotificationReleaseCollector.ReleaseContainer(releases, releases);
      doReturn(List.of(ArtistDtoFactory.createDefault())).when(followArtistService).getFollowedArtistsOfUser(any());
      doReturn(releases).when(releaseService).findAllReleases(anyList(), any());

      // when
      var result = underTest.fetchReleasesForUserAndFrequency(USER, 666, false);

      // then
      assertThat(result).isEqualTo(expectedReleaseContainer);
    }

    @Test
    @DisplayName("only releases with state 'OK' are returned")
    void test_release_state_ok() {
      // given
      var defaultRelease = ReleaseDtoFactory.createDefault();
      var duplicateRelease = ReleaseDtoFactory.createDefault();
      duplicateRelease.setState("DUPLICATE");
      var releases = List.of(defaultRelease, duplicateRelease);
      doReturn(List.of(ArtistDtoFactory.createDefault())).when(followArtistService).getFollowedArtistsOfUser(any());
      doReturn(releases).when(releaseService).findAllReleases(anyList(), any());

      // when
      var result = underTest.fetchReleasesForUserAndFrequency(USER, 666, false);

      // then
      assertThat(result.getRecentReleases()).containsExactly(defaultRelease);
      assertThat(result.getUpcomingReleases()).containsExactly(defaultRelease);
    }

    @Test
    @DisplayName("reissues are returned if configured")
    void test_reissues_returned() {
      // given
      var defaultRelease = ReleaseDtoFactory.createDefault();
      var reissue = ReleaseDtoFactory.createDefault();
      reissue.setReissue(true);
      var releases = List.of(defaultRelease, reissue);
      doReturn(List.of(ArtistDtoFactory.createDefault())).when(followArtistService).getFollowedArtistsOfUser(any());
      doReturn(releases).when(releaseService).findAllReleases(anyList(), any());

      // when
      var result = underTest.fetchReleasesForUserAndFrequency(USER, 666, true);

      // then
      assertThat(result.getRecentReleases()).containsAll(releases);
      assertThat(result.getUpcomingReleases()).containsAll(releases);
    }

    @Test
    @DisplayName("reissues are not returned if configured")
    void test_reissues_not_returned() {
      // given
      var defaultRelease = ReleaseDtoFactory.createDefault();
      var reissue = ReleaseDtoFactory.createDefault();
      reissue.setReissue(true);
      var releases = List.of(defaultRelease, reissue);
      doReturn(List.of(ArtistDtoFactory.createDefault())).when(followArtistService).getFollowedArtistsOfUser(any());
      doReturn(releases).when(releaseService).findAllReleases(anyList(), any());

      // when
      var result = underTest.fetchReleasesForUserAndFrequency(USER, 666, false);

      // then
      assertThat(result.getRecentReleases()).containsExactly(defaultRelease);
      assertThat(result.getUpcomingReleases()).containsExactly(defaultRelease);
    }
  }

  @Nested
  @DisplayName("Tests for todays releases")
  class TodaysReleases {

    @Test
    @DisplayName("followArtistService is called")
    void test_follow_artist_service_called() {
      // when
      underTest.fetchTodaysReleaseForUser(USER, false);

      // then
      verify(followArtistService).getFollowedArtistsOfUser(USER);
    }

    @Test
    @DisplayName("releaseService is called for user's todays releases")
    void test_release_service_called() {
      // given
      ArgumentCaptor<TimeRange> argumentCaptor = ArgumentCaptor.forClass(TimeRange.class);
      TemporalUnitLessThanOffset offset = new TemporalUnitLessThanOffset(1, DAYS);
      var now = LocalDate.now();
      var followedArtist = ArtistDtoFactory.createDefault();
      doReturn(List.of(followedArtist)).when(followArtistService).getFollowedArtistsOfUser(any());
      doReturn(List.of(ReleaseDtoFactory.createDefault())).when(releaseService).findAllReleases(anyList(), any());

      // when
      underTest.fetchTodaysReleaseForUser(USER, false);

      // then
      verify(releaseService).findAllReleases(eq(List.of(followedArtist.getArtistName())), argumentCaptor.capture());
      var timeRange = argumentCaptor.getValue();
      assertThat(timeRange.getDateFrom()).isCloseTo(now, offset);
      assertThat(timeRange.getDateTo()).isCloseTo(now, offset);
    }

    @Test
    @DisplayName("releaseService is not called if not followed artists exist")
    void test_release_service_not_called() {
      // given
      doReturn(Collections.emptyList()).when(followArtistService).getFollowedArtistsOfUser(any());

      // when
      underTest.fetchTodaysReleaseForUser(USER, false);

      // then
      verifyNoInteractions(releaseService);
    }

    @Test
    @DisplayName("releases are returned")
    void test_releases_returned() {
      // given
      var releases = List.of(ReleaseDtoFactory.createDefault());
      doReturn(List.of(ArtistDtoFactory.createDefault())).when(followArtistService).getFollowedArtistsOfUser(any());
      doReturn(releases).when(releaseService).findAllReleases(anyList(), any());

      // when
      var result = underTest.fetchTodaysReleaseForUser(USER, false);

      // then
      assertThat(result).isEqualTo(releases);
    }

    @Test
    @DisplayName("only releases with state 'OK' are returned")
    void test_release_state_ok() {
      // given
      var defaultRelease = ReleaseDtoFactory.createDefault();
      var duplicateRelease = ReleaseDtoFactory.createDefault();
      duplicateRelease.setState("DUPLICATE");
      var releases = List.of(defaultRelease, duplicateRelease);
      doReturn(List.of(ArtistDtoFactory.createDefault())).when(followArtistService).getFollowedArtistsOfUser(any());
      doReturn(releases).when(releaseService).findAllReleases(anyList(), any());

      // when
      var result = underTest.fetchTodaysReleaseForUser(USER, false);

      // then
      assertThat(result).containsExactly(defaultRelease);
    }

    @Test
    @DisplayName("reissues are returned if configured")
    void test_reissues_returned() {
      // given
      var defaultRelease = ReleaseDtoFactory.createDefault();
      var reissue = ReleaseDtoFactory.createDefault();
      reissue.setReissue(true);
      var releases = List.of(defaultRelease, reissue);
      doReturn(List.of(ArtistDtoFactory.createDefault())).when(followArtistService).getFollowedArtistsOfUser(any());
      doReturn(releases).when(releaseService).findAllReleases(anyList(), any());

      // when
      var result = underTest.fetchTodaysReleaseForUser(USER, true);

      // then
      assertThat(result).containsAll(releases);
    }

    @Test
    @DisplayName("reissues are not returned if configured")
    void test_reissues_not_returned() {
      // given
      var defaultRelease = ReleaseDtoFactory.createDefault();
      var reissue = ReleaseDtoFactory.createDefault();
      reissue.setReissue(true);
      var releases = List.of(defaultRelease, reissue);
      doReturn(List.of(ArtistDtoFactory.createDefault())).when(followArtistService).getFollowedArtistsOfUser(any());
      doReturn(releases).when(releaseService).findAllReleases(anyList(), any());

      // when
      var result = underTest.fetchTodaysReleaseForUser(USER, false);

      // then
      assertThat(result).containsExactly(defaultRelease);
    }
  }

  @Nested
  @DisplayName("Tests for todays announcements")
  class TodaysAnnouncements {

    @Test
    @DisplayName("followArtistService is called")
    void test_follow_artist_service_called() {
      // when
      underTest.fetchTodaysAnnouncementsForUser(USER, false);

      // then
      verify(followArtistService).getFollowedArtistsOfUser(USER);
    }

    @Test
    @DisplayName("releaseService is called for user's todays releases")
    void test_release_service_called() {
      // given
      ArgumentCaptor<TimeRange> argumentCaptor = ArgumentCaptor.forClass(TimeRange.class);
      TemporalUnitLessThanOffset offset = new TemporalUnitLessThanOffset(1, DAYS);
      var now = LocalDate.now();
      var followedArtist = ArtistDtoFactory.createDefault();
      doReturn(List.of(followedArtist)).when(followArtistService).getFollowedArtistsOfUser(any());
      doReturn(List.of(ReleaseDtoFactory.withAnnouncementDate(now))).when(releaseService).findAllReleases(anyList(), any());

      // when
      underTest.fetchTodaysAnnouncementsForUser(USER, false);

      // then
      verify(releaseService).findAllReleases(eq(List.of(followedArtist.getArtistName())), argumentCaptor.capture());
      var timeRange = argumentCaptor.getValue();
      assertThat(timeRange.getDateFrom()).isCloseTo(now, offset);
      assertThat(timeRange.getDateTo()).isNull();
    }

    @Test
    @DisplayName("releaseService is not called if not followed artists exist")
    void test_release_service_not_called() {
      // given
      doReturn(Collections.emptyList()).when(followArtistService).getFollowedArtistsOfUser(any());

      // when
      underTest.fetchTodaysAnnouncementsForUser(USER, false);

      // then
      verifyNoInteractions(releaseService);
    }

    @Test
    @DisplayName("today's announcements are returned")
    void test_releases_returned() {
      // given
      var now = LocalDate.now();
      var todaysAnnouncement = ReleaseDtoFactory.withAnnouncementDate(now);
      var releases = List.of(todaysAnnouncement, ReleaseDtoFactory.withAnnouncementDate(now.minusDays(1)));
      doReturn(List.of(ArtistDtoFactory.createDefault())).when(followArtistService).getFollowedArtistsOfUser(any());
      doReturn(releases).when(releaseService).findAllReleases(anyList(), any());

      // when
      List<ReleaseDto> result;
      try (MockedStatic<LocalDate> mock = mockStatic(LocalDate.class)) {
        mock.when(LocalDate::now).thenReturn(now);
        result = underTest.fetchTodaysAnnouncementsForUser(USER, false);
      }

      // then
      assertThat(result).containsExactly(todaysAnnouncement);
    }

    @Test
    @DisplayName("today's announcements with state 'OK' are returned")
    void test_release_state_ok() {
      // given
      var now = LocalDate.now();
      var todaysAnnouncement = ReleaseDtoFactory.withAnnouncementDate(now);
      var duplicateAnnouncement = ReleaseDtoFactory.withAnnouncementDate(now);
      duplicateAnnouncement.setState("DUPLICATE");
      var releases = List.of(todaysAnnouncement, ReleaseDtoFactory.withAnnouncementDate(now.minusDays(1)), duplicateAnnouncement);
      doReturn(List.of(ArtistDtoFactory.createDefault())).when(followArtistService).getFollowedArtistsOfUser(any());
      doReturn(releases).when(releaseService).findAllReleases(anyList(), any());

      // when
      List<ReleaseDto> result;
      try (MockedStatic<LocalDate> mock = mockStatic(LocalDate.class)) {
        mock.when(LocalDate::now).thenReturn(now);
        result = underTest.fetchTodaysAnnouncementsForUser(USER, false);
      }

      // then
      assertThat(result).containsExactly(todaysAnnouncement);
    }

    @Test
    @DisplayName("reissues are returned if configured")
    void test_reissues_returned() {
      // given
      var now = LocalDate.now();
      var todaysAnnouncement = ReleaseDtoFactory.withAnnouncementDate(now);
      var reissue = ReleaseDtoFactory.withAnnouncementDate(now);
      reissue.setReissue(true);
      var releases = List.of(todaysAnnouncement, ReleaseDtoFactory.withAnnouncementDate(now.minusDays(1)), reissue);
      doReturn(List.of(ArtistDtoFactory.createDefault())).when(followArtistService).getFollowedArtistsOfUser(any());
      doReturn(releases).when(releaseService).findAllReleases(anyList(), any());

      // when
      List<ReleaseDto> result;
      try (MockedStatic<LocalDate> mock = mockStatic(LocalDate.class)) {
        mock.when(LocalDate::now).thenReturn(now);
        result = underTest.fetchTodaysAnnouncementsForUser(USER, true);
      }

      // then
      assertThat(result).containsAll(List.of(todaysAnnouncement, reissue));
    }

    @Test
    @DisplayName("reissues are not returned if configured")
    void test_reissues_not_returned() {
      // given
      var now = LocalDate.now();
      var todaysAnnouncement = ReleaseDtoFactory.withAnnouncementDate(now);
      var reissue = ReleaseDtoFactory.withAnnouncementDate(now);
      reissue.setReissue(true);
      var releases = List.of(todaysAnnouncement, ReleaseDtoFactory.withAnnouncementDate(now.minusDays(1)), reissue);
      doReturn(List.of(ArtistDtoFactory.createDefault())).when(followArtistService).getFollowedArtistsOfUser(any());
      doReturn(releases).when(releaseService).findAllReleases(anyList(), any());

      // when
      List<ReleaseDto> result;
      try (MockedStatic<LocalDate> mock = mockStatic(LocalDate.class)) {
        mock.when(LocalDate::now).thenReturn(now);
        result = underTest.fetchTodaysAnnouncementsForUser(USER, false);
      }

      // then
      assertThat(result).containsExactly(todaysAnnouncement);
    }
  }
}
