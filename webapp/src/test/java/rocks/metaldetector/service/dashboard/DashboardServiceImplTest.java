package rocks.metaldetector.service.dashboard;

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
import org.mockito.junit.jupiter.MockitoExtension;
import rocks.metaldetector.service.artist.FollowArtistService;
import rocks.metaldetector.support.TimeRange;
import rocks.metaldetector.testutil.DtoFactory.ArtistDtoFactory;
import rocks.metaldetector.testutil.DtoFactory.ReleaseDtoFactory;

import java.time.LocalDate;
import java.util.List;

import static java.time.temporal.ChronoUnit.DAYS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static rocks.metaldetector.service.dashboard.DashboardServiceImpl.MIN_FOLLOWER;
import static rocks.metaldetector.service.dashboard.DashboardServiceImpl.RESULT_LIMIT;
import static rocks.metaldetector.service.dashboard.DashboardServiceImpl.TIME_RANGE_MONTHS;

@ExtendWith(MockitoExtension.class)
class DashboardServiceImplTest implements WithAssertions {

  @Mock
  private ReleaseCollector releaseCollector;

  @Mock
  private ArtistCollector artistCollector;

  @Mock
  private FollowArtistService followArtistService;

  @InjectMocks
  private DashboardServiceImpl underTest;

  @AfterEach
  void tearDown() {
    reset(releaseCollector, artistCollector, followArtistService);
  }

  @Nested
  @DisplayName("Tests for getting dashboard")
  class DashboardTest {

    @Test
    @DisplayName("followArtistService is called to get current user's followed artists")
    void test_follow_artist_service_called() {
      // when
      underTest.createDashboardResponse();

      // then
      verify(followArtistService).getFollowedArtistsOfCurrentUser();
    }

    @Test
    @DisplayName("releaseCollector is called with followed artists to get upcoming releases")
    void test_release_collector_upcoming_releases() {
      // given
      var artists = List.of(ArtistDtoFactory.createDefault());
      doReturn(artists).when(followArtistService).getFollowedArtistsOfCurrentUser();

      // when
      underTest.createDashboardResponse();

      // then
      verify(releaseCollector).collectUpcomingReleases(eq(artists));
    }

    @Test
    @DisplayName("releaseCollector is called with followed artists to get recent releases")
    void test_release_collector_recent_releases() {
      // given
      var artists = List.of(ArtistDtoFactory.createDefault());
      doReturn(artists).when(followArtistService).getFollowedArtistsOfCurrentUser();

      // when
      underTest.createDashboardResponse();

      // then
      verify(releaseCollector).collectRecentReleases(eq(artists));
    }

    @Test
    @DisplayName("releaseCollector is called to get most expected releases")
    void test_release_collector_most_expected_releases() {
      // given
      ArgumentCaptor<TimeRange> argumentCaptor = ArgumentCaptor.forClass(TimeRange.class);
      TemporalUnitLessThanOffset offset = new TemporalUnitLessThanOffset(1, DAYS);
      var now = LocalDate.now();
      var expectedTimeRange = new TimeRange(now, now.plusMonths(TIME_RANGE_MONTHS));
      var artist = ArtistDtoFactory.createDefault();
      var artists = List.of(artist, artist, artist, artist, artist);
      doReturn(artists).when(artistCollector).collectTopFollowedArtists(anyInt());

      // when
      underTest.createDashboardResponse();

      // then
      verify(releaseCollector).collectTopReleases(argumentCaptor.capture(), eq(artists), eq(RESULT_LIMIT));
      var timeRange = argumentCaptor.getValue();
      assertThat(timeRange.getDateFrom()).isCloseTo(expectedTimeRange.getDateFrom(), offset);
      assertThat(timeRange.getDateTo()).isCloseTo(expectedTimeRange.getDateTo(), offset);
    }

    @Test
    @DisplayName("artistCollector is called to get top followed artists")
    void test_artist_collector_top_artists() {
      // when
      underTest.createDashboardResponse();

      // then
      verify(artistCollector).collectTopFollowedArtists(MIN_FOLLOWER);
    }

    @Test
    @DisplayName("artistCollector is called to get recently followed artists")
    void test_artist_collector_recently_artists() {
      // when
      underTest.createDashboardResponse();

      // then
      verify(artistCollector).collectRecentlyFollowedArtists(RESULT_LIMIT);
    }

    @Test
    @DisplayName("upcoming releases are returned")
    void test_upcoming_releases_returned() {
      // given
      var releases = List.of(ReleaseDtoFactory.createDefault());
      doReturn(releases).when(releaseCollector).collectUpcomingReleases(anyList());

      // when
      var result = underTest.createDashboardResponse();

      // then
      assertThat(result.getUpcomingReleases()).isEqualTo(releases);
    }

    @Test
    @DisplayName("recent releases are returned")
    void test_recent_releases_returned() {
      // given
      var releases = List.of(ReleaseDtoFactory.createDefault());
      doReturn(releases).when(releaseCollector).collectRecentReleases(anyList());

      // when
      var result = underTest.createDashboardResponse();

      // then
      assertThat(result.getRecentReleases()).isEqualTo(releases);
    }

    @Test
    @DisplayName("top followed artists are returned")
    void test_top_followed_artists_returned() {
      // given
      var artist = ArtistDtoFactory.createDefault();
      var artists = List.of(artist, artist, artist, artist, artist);
      doReturn(artists).when(artistCollector).collectTopFollowedArtists(anyInt());

      // when
      var result = underTest.createDashboardResponse();

      // then
      assertThat(result.getFavoriteCommunityArtists()).isEqualTo(artists);
    }

    @Test
    @DisplayName("most expected releases are returned")
    void test_most_expected_releases_returned() {
      // given
      var releases = List.of(ReleaseDtoFactory.createDefault());
      doReturn(releases).when(releaseCollector).collectTopReleases(any(), any(), anyInt());

      // when
      var result = underTest.createDashboardResponse();

      // then
      assertThat(result.getMostExpectedReleases()).isEqualTo(releases);
    }

    @Test
    @DisplayName("recently followed artists are returned")
    void test_recently_followed_artists_returned() {
      // given
      var artists = List.of(ArtistDtoFactory.createDefault());
      doReturn(artists).when(artistCollector).collectRecentlyFollowedArtists(anyInt());

      // when
      var result = underTest.createDashboardResponse();

      // then
      assertThat(result.getRecentlyFollowedArtists()).isEqualTo(artists);
    }
  }

  @Nested
  @DisplayName("Tests for top releases")
  class TopReleasesTest {

    @Test
    @DisplayName("artistCollector is called")
    void test_artist_collector_called() {
      // given
      var minFollower = 55;

      // when
      underTest.findTopReleases(new TimeRange(LocalDate.now(), LocalDate.now()), minFollower, 66);

      // then
      verify(artistCollector).collectTopFollowedArtists(minFollower);
    }

    @Test
    @DisplayName("releaseCollector is called")
    void test_release_collector_called() {
      // given
      var timeRange = new TimeRange(LocalDate.now(), LocalDate.now());
      var artists = List.of(ArtistDtoFactory.createDefault());
      var maxReleases = 66;
      doReturn(artists).when(artistCollector).collectTopFollowedArtists(anyInt());

      // when
      underTest.findTopReleases(timeRange, 55, maxReleases);

      // then
      verify(releaseCollector).collectTopReleases(timeRange, artists, maxReleases);
    }

    @Test
    @DisplayName("releases are returned")
    void test_releases_returned() {
      // given
      var releases = List.of(ReleaseDtoFactory.createDefault());
      doReturn(releases).when(releaseCollector).collectTopReleases(any(), any(), anyInt());

      // when
      var result = underTest.findTopReleases(new TimeRange(), 1, 10);

      // then
      assertThat(result).isEqualTo(releases);
    }
  }
}
