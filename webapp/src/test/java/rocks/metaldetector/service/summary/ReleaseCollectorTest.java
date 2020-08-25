package rocks.metaldetector.service.summary;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rocks.metaldetector.butler.facade.ReleaseService;
import rocks.metaldetector.service.artist.FollowArtistService;
import rocks.metaldetector.support.PageRequest;
import rocks.metaldetector.support.TimeRange;
import rocks.metaldetector.testutil.DtoFactory;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static rocks.metaldetector.service.summary.SummaryServiceImpl.RESULT_LIMIT;
import static rocks.metaldetector.service.summary.SummaryServiceImpl.TIME_RANGE_MONTHS;

@ExtendWith(MockitoExtension.class)
class ReleaseCollectorTest implements WithAssertions {

  @Mock
  private ReleaseService releaseService;

  @Mock
  private FollowArtistService followArtistService;

  @InjectMocks
  private ReleaseCollector underTest;

  @Test
  @DisplayName("collecting upcoming releases calls followArtistService")
  void test_upcoming_releases_calls_follow_artist_service() {
    // given
    doReturn(List.of(DtoFactory.ArtistDtoFactory.withName("A"))).when(followArtistService).getFollowedArtistsOfCurrentUser();

    // when
    underTest.collectUpcomingReleases();

    // then
    verify(followArtistService, times(1)).getFollowedArtistsOfCurrentUser();
  }

  @Test
  @DisplayName("collecting upcoming releases does not call releaseService when no artists are followed")
  void test_upcoming_releases_does_not_call_release_service() {
    // given
    doReturn(Collections.emptyList()).when(followArtistService).getFollowedArtistsOfCurrentUser();

    // when
    underTest.collectUpcomingReleases();

    // then
    verifyNoInteractions(releaseService);
  }

  @Test
  @DisplayName("collecting upcoming releases calls releaseService with followed artists' names")
  void test_upcoming_releases_calls_release_service_with_artist_names() {
    // given
    var expectedArtistNames = List.of("A");
    doReturn(List.of(DtoFactory.ArtistDtoFactory.withName("A"))).when(followArtistService).getFollowedArtistsOfCurrentUser();

    // when
    underTest.collectUpcomingReleases();

    // then
    verify(releaseService, times(1)).findReleases(eq(expectedArtistNames), any(), any());
  }

  @Test
  @DisplayName("collecting upcoming releases calls releaseService with correct time range")
  void test_upcoming_releases_calls_release_service_with_time_range() {
    // given
    var now = LocalDate.now();
    var expectedTimeRange = new TimeRange(now, now.plusMonths(TIME_RANGE_MONTHS));
    doReturn(List.of(DtoFactory.ArtistDtoFactory.withName("A"))).when(followArtistService).getFollowedArtistsOfCurrentUser();

    // when
    underTest.collectUpcomingReleases();

    // then
    verify(releaseService, times(1)).findReleases(any(), eq(expectedTimeRange), any());
  }

  @Test
  @DisplayName("collecting upcoming releases calls releaseService with correct page request")
  void test_upcoming_releases_calls_release_service_with_page_request() {
    // given
    var expectedPageRequest = new PageRequest(1, RESULT_LIMIT);
    doReturn(List.of(DtoFactory.ArtistDtoFactory.withName("A"))).when(followArtistService).getFollowedArtistsOfCurrentUser();

    // when
    underTest.collectUpcomingReleases();

    // then
    verify(releaseService, times(1)).findReleases(any(), any(), eq(expectedPageRequest));
  }

  @Test
  @DisplayName("collecting recent releases calls followArtistService")
  void test_recent_releases_calls_follow_artist_service() {
    // given
    doReturn(List.of(DtoFactory.ArtistDtoFactory.withName("A"))).when(followArtistService).getFollowedArtistsOfCurrentUser();

    // when
    underTest.collectRecentReleases();

    // then
    verify(followArtistService, times(1)).getFollowedArtistsOfCurrentUser();
  }

  @Test
  @DisplayName("collecting recent releases does not call releaseService when no artists are followed")
  void test_recent_releases_does_not_call_release_service() {
    // given
    doReturn(Collections.emptyList()).when(followArtistService).getFollowedArtistsOfCurrentUser();

    // when
    underTest.collectRecentReleases();

    // then
    verifyNoInteractions(releaseService);
  }

  @Test
  @DisplayName("collecting recent releases calls releaseService with followed artists' names")
  void test_recent_releases_calls_release_service_with_artist_names() {
    // given
    var expectedArtistNames = List.of("A");
    doReturn(List.of(DtoFactory.ArtistDtoFactory.withName("A"))).when(followArtistService).getFollowedArtistsOfCurrentUser();

    // when
    underTest.collectRecentReleases();

    // then
    verify(releaseService, times(1)).findReleases(eq(expectedArtistNames), any(), any());
  }

  @Test
  @DisplayName("collecting recent releases calls releaseService with correct time range")
  void test_recent_releases_calls_release_service_with_time_range() {
    // given
    var now = LocalDate.now();
    var expectedTimeRange = new TimeRange(now.minusMonths(TIME_RANGE_MONTHS), now);
    doReturn(List.of(DtoFactory.ArtistDtoFactory.withName("A"))).when(followArtistService).getFollowedArtistsOfCurrentUser();

    // when
    underTest.collectRecentReleases();

    // then
    verify(releaseService, times(1)).findReleases(any(), eq(expectedTimeRange), any());
  }

  @Test
  @DisplayName("collecting recent releases calls releaseService with correct page request")
  void test_recent_releases_calls_release_service_with_page_request() {
    // given
    var expectedPageRequest = new PageRequest(1, RESULT_LIMIT);
    doReturn(List.of(DtoFactory.ArtistDtoFactory.withName("A"))).when(followArtistService).getFollowedArtistsOfCurrentUser();

    // when
    underTest.collectRecentReleases();

    // then
    verify(releaseService, times(1)).findReleases(any(), any(), eq(expectedPageRequest));
  }
}
