package rocks.metaldetector.service.summary;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rocks.metaldetector.butler.facade.ReleaseService;
import rocks.metaldetector.support.DetectorSort;
import rocks.metaldetector.support.Page;
import rocks.metaldetector.support.PageRequest;
import rocks.metaldetector.support.Pagination;
import rocks.metaldetector.support.TimeRange;
import rocks.metaldetector.testutil.DtoFactory.ArtistDtoFactory;
import rocks.metaldetector.testutil.DtoFactory.ReleaseDtoFactory;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static rocks.metaldetector.service.summary.SummaryServiceImpl.RESULT_LIMIT;
import static rocks.metaldetector.service.summary.SummaryServiceImpl.TIME_RANGE_MONTHS;
import static rocks.metaldetector.support.DetectorSort.Direction.ASC;
import static rocks.metaldetector.support.DetectorSort.Direction.DESC;

@ExtendWith(MockitoExtension.class)
class ReleaseCollectorTest implements WithAssertions {

  @Mock
  private ReleaseService releaseService;

  @InjectMocks
  private ReleaseCollector underTest;

  @AfterEach
  void tearDown() {
    reset(releaseService);
  }

  @Test
  @DisplayName("collecting upcoming releases does not call releaseService when no artists are given")
  void test_upcoming_releases_does_not_call_release_service() {
    // when
    underTest.collectUpcomingReleases(Collections.emptyList());

    // then
    verifyNoInteractions(releaseService);
  }

  @Test
  @DisplayName("collecting upcoming releases calls releaseService with followed artists' names")
  void test_upcoming_releases_calls_release_service_with_artist_names() {
    // given
    var expectedArtistNames = List.of("A");
    var artists = List.of(ArtistDtoFactory.withName("A"));
    doReturn(new Page<>(Collections.emptyList(), new Pagination())).when(releaseService).findReleases(any(), any(), any(), any());

    // when
    underTest.collectUpcomingReleases(artists);

    // then
    verify(releaseService).findReleases(eq(expectedArtistNames), any(), any(), any());
  }

  @Test
  @DisplayName("collecting upcoming releases calls releaseService with correct time range")
  void test_upcoming_releases_calls_release_service_with_time_range() {
    // given
    var tomorrow = LocalDate.now().plusDays(1);
    var expectedTimeRange = new TimeRange(tomorrow, tomorrow.plusMonths(TIME_RANGE_MONTHS));
    var artists = List.of(ArtistDtoFactory.withName("A"));
    doReturn(new Page<>(Collections.emptyList(), new Pagination())).when(releaseService).findReleases(any(), any(), any(), any());

    // when
    underTest.collectUpcomingReleases(artists);

    // then
    verify(releaseService).findReleases(any(), eq(expectedTimeRange), any(), any());
  }

  @Test
  @DisplayName("collecting upcoming releases calls releaseService without query")
  void test_upcoming_releases_calls_release_service_without_query() {
    // given
    var artists = List.of(ArtistDtoFactory.withName("A"));
    doReturn(new Page<>(Collections.emptyList(), new Pagination())).when(releaseService).findReleases(any(), any(), any(), any());

    // when
    underTest.collectUpcomingReleases(artists);

    // then
    verify(releaseService).findReleases(any(), any(), eq(null), any());
  }

  @Test
  @DisplayName("collecting upcoming releases calls releaseService with correct page request and sorting")
  void test_upcoming_releases_calls_release_service_with_page_request() {
    // given
    var sorting = new DetectorSort("releaseDate", ASC);
    var expectedPageRequest = new PageRequest(1, RESULT_LIMIT, sorting);
    var artists = List.of(ArtistDtoFactory.withName("A"));
    doReturn(new Page<>(Collections.emptyList(), new Pagination())).when(releaseService).findReleases(any(), any(), any(), any());

    // when
    underTest.collectUpcomingReleases(artists);

    // then
    verify(releaseService).findReleases(any(), any(), any(), eq(expectedPageRequest));
  }

  @Test
  @DisplayName("collecting upcoming releases returns list of releases")
  void test_upcoming_returns_releases() {
    // given
    var artists = List.of(ArtistDtoFactory.withName("A"));
    var releases = List.of(ReleaseDtoFactory.createDefault(), ReleaseDtoFactory.createDefault());
    doReturn(new Page<>(releases, new Pagination())).when(releaseService).findReleases(any(), any(), any(), any());

    // when
    var result = underTest.collectUpcomingReleases(artists);

    // then
    assertThat(result).isEqualTo(releases);
  }

  @Test
  @DisplayName("collecting recent releases does not call releaseService when no artists are given")
  void test_recent_releases_does_not_call_release_service() {
    // when
    underTest.collectRecentReleases(Collections.emptyList());

    // then
    verifyNoInteractions(releaseService);
  }

  @Test
  @DisplayName("collecting recent releases calls releaseService with followed artists' names")
  void test_recent_releases_calls_release_service_with_artist_names() {
    // given
    var expectedArtistNames = List.of("A");
    var artists = List.of(ArtistDtoFactory.withName("A"));
    doReturn(new Page<>(Collections.emptyList(), new Pagination())).when(releaseService).findReleases(any(), any(), any(), any());

    // when
    underTest.collectRecentReleases(artists);

    // then
    verify(releaseService).findReleases(eq(expectedArtistNames), any(), any(), any());
  }

  @Test
  @DisplayName("collecting recent releases calls releaseService with correct time range")
  void test_recent_releases_calls_release_service_with_time_range() {
    // given
    var now = LocalDate.now();
    var expectedTimeRange = new TimeRange(now.minusMonths(TIME_RANGE_MONTHS), now);
    var artists = List.of(ArtistDtoFactory.withName("A"));
    doReturn(new Page<>(Collections.emptyList(), new Pagination())).when(releaseService).findReleases(any(), any(), any(), any());

    // when
    underTest.collectRecentReleases(artists);

    // then
    verify(releaseService).findReleases(any(), eq(expectedTimeRange), any(), any());
  }

  @Test
  @DisplayName("collecting recent releases calls releaseService without query")
  void test_recent_releases_calls_release_service_without_query() {
    // given
    var artists = List.of(ArtistDtoFactory.withName("A"));
    doReturn(new Page<>(Collections.emptyList(), new Pagination())).when(releaseService).findReleases(any(), any(), any(), any());

    // when
    underTest.collectRecentReleases(artists);

    // then
    verify(releaseService).findReleases(any(), any(), eq(null), any());
  }

  @Test
  @DisplayName("collecting recent releases calls releaseService with correct page request and sorting")
  void test_recent_releases_calls_release_service_with_page_request() {
    // given
    var expectedSorting = new DetectorSort("releaseDate", DESC);
    var expectedPageRequest = new PageRequest(1, RESULT_LIMIT, expectedSorting);
    var artists = List.of(ArtistDtoFactory.withName("A"));
    doReturn(new Page<>(Collections.emptyList(), new Pagination())).when(releaseService).findReleases(any(), any(), any(), any());

    // when
    underTest.collectRecentReleases(artists);

    // then
    verify(releaseService).findReleases(any(), any(), any(), eq(expectedPageRequest));
  }

  @Test
  @DisplayName("collecting top releases does not call releaseService when no artists are given")
  void test_top_releases_does_not_call_release_service() {
    // when
    underTest.collectTopReleases(new TimeRange(), Collections.emptyList(), 10);

    // then
    verifyNoInteractions(releaseService);
  }

  @Test
  @DisplayName("collecting top releases calls releaseService with top followed artists' names")
  void test_top_releases_calls_release_service_with_top_artist_names() {
    // given
    var expectedArtistNames = List.of("A");
    var artists = List.of(ArtistDtoFactory.withName("A"));
    doReturn(new Page<>(Collections.emptyList(), new Pagination())).when(releaseService).findReleases(any(), any(), any(), any());

    // when
    underTest.collectTopReleases(new TimeRange(), artists, 10);

    // then
    verify(releaseService).findReleases(eq(expectedArtistNames), any(), any(), any());
  }

  @Test
  @DisplayName("collecting top releases calls releaseService with given time range")
  void test_top_releases_calls_release_service_with_time_range() {
    // given
    var now = LocalDate.now();
    var timeRange = new TimeRange(now, now.plusDays(666));
    var artists = List.of(ArtistDtoFactory.withName("A"));
    doReturn(new Page<>(Collections.emptyList(), new Pagination())).when(releaseService).findReleases(any(), any(), any(), any());

    // when
    underTest.collectTopReleases(timeRange, artists, 10);

    // then
    verify(releaseService).findReleases(any(), eq(timeRange), any(), any());
  }

  @Test
  @DisplayName("collecting top releases calls releaseService without query")
  void test_top_releases_calls_release_service_without_query() {
    // given
    var artists = List.of(ArtistDtoFactory.withName("A"));
    doReturn(new Page<>(Collections.emptyList(), new Pagination())).when(releaseService).findReleases(any(), any(), any(), any());

    // when
    underTest.collectTopReleases(new TimeRange(), artists, 10);

    // then
    verify(releaseService).findReleases(any(), any(), eq(null), any());
  }

  @Test
  @DisplayName("collecting top releases calls releaseService with correct page request and sorting")
  void test_top_releases_calls_release_service_with_page_request() {
    // given
    var sorting = new DetectorSort("artist", ASC);
    var expectedPageRequest = new PageRequest(1, RESULT_LIMIT, sorting);
    var artists = List.of(ArtistDtoFactory.withName("A"));
    doReturn(new Page<>(Collections.emptyList(), new Pagination())).when(releaseService).findReleases(any(), any(), any(), any());

    // when
    underTest.collectTopReleases(new TimeRange(), artists, 10);

    // then
    verify(releaseService).findReleases(any(), any(), any(), eq(expectedPageRequest));
  }

  @Test
  @DisplayName("collecting top releases fetches releases with most followers first")
  void test_top_releases_most_followed() {
    // given
    var artist1 = ArtistDtoFactory.withName("a");
    var artist2 = ArtistDtoFactory.withName("b");
    artist1.setFollower(5);
    artist2.setFollower(10);
    var release1 = ReleaseDtoFactory.withArtistName(artist1.getArtistName());
    var release2 = ReleaseDtoFactory.withArtistName(artist2.getArtistName());
    var artists = List.of(artist1, artist2);
    doReturn(new Page<>(List.of(release2, release1), new Pagination())).when(releaseService).findReleases(any(), any(), any(), any());

    // when
    var result = underTest.collectTopReleases(new TimeRange(), artists, 1);

    // then
    assertThat(result).containsExactly(release2);
  }

  @Test
  @DisplayName("collecting top releases sorts by release date")
  void test_top_releases_sorted() {
    // given
    var artist1 = ArtistDtoFactory.withName("a");
    var artist2 = ArtistDtoFactory.withName("b");
    var release1 = ReleaseDtoFactory.withArtistName(artist1.getArtistName());
    var release2 = ReleaseDtoFactory.withArtistName(artist2.getArtistName());
    release2.setReleaseDate(LocalDate.now().minusDays(10));
    var artists = List.of(artist1, artist2);
    doReturn(new Page<>(List.of(release2, release1), new Pagination())).when(releaseService).findReleases(any(), any(), any(), any());

    // when
    var result = underTest.collectTopReleases(new TimeRange(), artists, 10);

    // then
    assertThat(result).hasSize(2);
    assertThat(result.get(0)).isEqualTo(release2);
    assertThat(result.get(1)).isEqualTo(release1);
  }

  @Test
  @DisplayName("collecting top releases limits to given value")
  void test_top_releases_limited() {
    // given
    var maxReleases = 1;
    var artist1 = ArtistDtoFactory.withName("a");
    var artist2 = ArtistDtoFactory.withName("b");
    artist1.setFollower(2);
    artist2.setFollower(1);
    var release1 = ReleaseDtoFactory.withArtistName(artist1.getArtistName());
    var release2 = ReleaseDtoFactory.withArtistName(artist2.getArtistName());
    var artists = List.of(artist1, artist2);
    doReturn(new Page<>(List.of(release2, release1), new Pagination())).when(releaseService).findReleases(any(), any(), any(), any());

    // when
    var result = underTest.collectTopReleases(new TimeRange(), artists, maxReleases);

    // then
    assertThat(result).hasSize(maxReleases);
    assertThat(result).containsExactly(release1);
  }
}
