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
import rocks.metaldetector.support.Page;
import rocks.metaldetector.support.PageRequest;
import rocks.metaldetector.support.Pagination;
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
import static rocks.metaldetector.service.notification.messaging.NotificationReleaseCollector.PAGE_SIZE;

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
      var expectedPageRequest = new PageRequest(1, PAGE_SIZE, null);
      doReturn(List.of(followedArtist)).when(followArtistService).getFollowedArtistsOfUser(any());
      doReturn(new Page<>(List.of(ReleaseDtoFactory.createDefault()), new Pagination()))
          .when(releaseService).findReleases(anyList(), any(), any(), any());

      // when
      underTest.fetchReleasesForUserAndFrequency(USER, frequency, false);

      // then
      verify(releaseService, times(2)).findReleases(eq(List.of(followedArtist.getArtistName())), argumentCaptor.capture(), eq(null), eq(expectedPageRequest));
      var timeRanges = argumentCaptor.getAllValues();
      assertThat(timeRanges.get(0).getDateFrom()).isCloseTo(timeRangeUpcoming.getDateFrom(), offset);
      assertThat(timeRanges.get(0).getDateTo()).isCloseTo(timeRangeUpcoming.getDateTo(), offset);
      assertThat(timeRanges.get(1).getDateFrom()).isCloseTo(timeRangeRecent.getDateFrom(), offset);
      assertThat(timeRanges.get(1).getDateTo()).isCloseTo(timeRangeRecent.getDateTo(), offset);
    }

    @Test
    @DisplayName("releaseService is called again for user's current and recent releases")
    void test_release_service_called_again_for_current_releases() {
      // given
      var expectedPageRequest1 = new PageRequest(1, PAGE_SIZE, null);
      var expectedPageRequest2 = new PageRequest(2, PAGE_SIZE, null);
      doReturn(List.of(ArtistDtoFactory.createDefault())).when(followArtistService).getFollowedArtistsOfUser(any());
      doReturn(new Page<>(List.of(ReleaseDtoFactory.createDefault()), new Pagination(2, 1, 1)))
          .when(releaseService).findReleases(anyList(), any(), any(), any());

      // when
      underTest.fetchReleasesForUserAndFrequency(USER, 6, false);

      // then
      verify(releaseService, times(2)).findReleases(any(), any(), any(), eq(expectedPageRequest1));
      verify(releaseService, times(2)).findReleases(any(), any(), any(), eq(expectedPageRequest2));
    }

    @Test
    @DisplayName("releaseService is not called if no followed artists exist")
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
      doReturn(new Page<>(releases, new Pagination())).when(releaseService).findReleases(any(), any(), any(), any());

      // when
      var result = underTest.fetchReleasesForUserAndFrequency(USER, 666, false);

      // then
      assertThat(result).isEqualTo(expectedReleaseContainer);
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
      doReturn(new Page<>(releases, new Pagination())).when(releaseService).findReleases(any(), any(), any(), any());

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
      doReturn(new Page<>(releases, new Pagination())).when(releaseService).findReleases(any(), any(), any(), any());

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
      var expectedPageRequest = new PageRequest(1, PAGE_SIZE, null);
      doReturn(List.of(followedArtist)).when(followArtistService).getFollowedArtistsOfUser(any());
      doReturn(new Page<>(List.of(ReleaseDtoFactory.createDefault()), new Pagination())).when(releaseService).findReleases(anyList(), any(), any(), any());

      // when
      underTest.fetchTodaysReleaseForUser(USER, false);

      // then
      verify(releaseService).findReleases(eq(List.of(followedArtist.getArtistName())), argumentCaptor.capture(), eq(null), eq(expectedPageRequest));
      var timeRange = argumentCaptor.getValue();
      assertThat(timeRange.getDateFrom()).isCloseTo(now, offset);
      assertThat(timeRange.getDateTo()).isCloseTo(now, offset);
    }

    @Test
    @DisplayName("releaseService is called again for user's todays releases")
    void test_release_service_called_again() {
      // given
      var expectedPageRequest1 = new PageRequest(1, PAGE_SIZE, null);
      var expectedPageRequest2 = new PageRequest(2, PAGE_SIZE, null);
      doReturn(List.of(ArtistDtoFactory.createDefault())).when(followArtistService).getFollowedArtistsOfUser(any());
      doReturn(new Page<>(List.of(ReleaseDtoFactory.createDefault()), new Pagination(2, 1, 1))).when(releaseService).findReleases(anyList(), any(), any(), any());

      // when
      underTest.fetchTodaysReleaseForUser(USER, false);

      // then
      verify(releaseService).findReleases(any(), any(), any(), eq(expectedPageRequest1));
      verify(releaseService).findReleases(any(), any(), any(), eq(expectedPageRequest2));
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
      doReturn(new Page<>(releases, new Pagination())).when(releaseService).findReleases(any(), any(), any(), any());

      // when
      var result = underTest.fetchTodaysReleaseForUser(USER, false);

      // then
      assertThat(result).isEqualTo(releases);
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
      doReturn(new Page<>(releases, new Pagination())).when(releaseService).findReleases(any(), any(), any(), any());

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
      doReturn(new Page<>(releases, new Pagination())).when(releaseService).findReleases(any(), any(), any(), any());

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
      var expectedPageRequest = new PageRequest(1, PAGE_SIZE, null);
      doReturn(List.of(followedArtist)).when(followArtistService).getFollowedArtistsOfUser(any());
      doReturn(new Page<>(List.of(ReleaseDtoFactory.withAnnouncementDate(now)), new Pagination())).when(releaseService).findReleases(anyList(), any(), any(), any());

      // when
      underTest.fetchTodaysAnnouncementsForUser(USER, false);

      // then
      verify(releaseService).findReleases(eq(List.of(followedArtist.getArtistName())), argumentCaptor.capture(), eq(null), eq(expectedPageRequest));
      var timeRange = argumentCaptor.getValue();
      assertThat(timeRange.getDateFrom()).isCloseTo(now, offset);
      assertThat(timeRange.getDateTo()).isNull();
    }

    @Test
    @DisplayName("releaseService is called again for user's todays releases")
    void test_release_service_called_again() {
      // given
      var expectedPageRequest1 = new PageRequest(1, PAGE_SIZE, null);
      var expectedPageRequest2 = new PageRequest(1, PAGE_SIZE, null);
      doReturn(List.of(ArtistDtoFactory.createDefault())).when(followArtistService).getFollowedArtistsOfUser(any());
      doReturn(new Page<>(List.of(ReleaseDtoFactory.withAnnouncementDate(LocalDate.now())), new Pagination(2, 1, 1))).when(releaseService).findReleases(anyList(), any(), any(), any());

      // when
      underTest.fetchTodaysAnnouncementsForUser(USER, false);

      // then
      verify(releaseService).findReleases(any(), any(), any(), eq(expectedPageRequest1));
      verify(releaseService).findReleases(any(), any(), any(), eq(expectedPageRequest2));
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
      doReturn(new Page<>(releases, new Pagination())).when(releaseService).findReleases(any(), any(), any(), any());

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
      doReturn(new Page<>(releases, new Pagination())).when(releaseService).findReleases(any(), any(), any(), any());

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
      doReturn(new Page<>(releases, new Pagination())).when(releaseService).findReleases(any(), any(), any(), any());

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
