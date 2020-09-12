package rocks.metaldetector.service.summary;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rocks.metaldetector.butler.facade.ReleaseService;
import rocks.metaldetector.support.PageRequest;
import rocks.metaldetector.support.TimeRange;
import rocks.metaldetector.testutil.DtoFactory.ArtistDtoFactory;
import rocks.metaldetector.testutil.DtoFactory.ReleaseDtoFactory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

  @InjectMocks
  private ReleaseCollector underTest;

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

    underTest.collectUpcomingReleases(artists);
    // when

    // then
    verify(releaseService, times(1)).findReleases(eq(expectedArtistNames), any(), any());
  }

  @Test
  @DisplayName("collecting upcoming releases calls releaseService with correct time range")
  void test_upcoming_releases_calls_release_service_with_time_range() {
    // given
    var now = LocalDate.now();
    var expectedTimeRange = new TimeRange(now, now.plusMonths(TIME_RANGE_MONTHS));
    var artists = List.of(ArtistDtoFactory.withName("A"));

    // when
    underTest.collectUpcomingReleases(artists);

    // then
    verify(releaseService, times(1)).findReleases(any(), eq(expectedTimeRange), any());
  }

  @Test
  @DisplayName("collecting upcoming releases calls releaseService with correct page request")
  void test_upcoming_releases_calls_release_service_with_page_request() {
    // given
    var expectedPageRequest = new PageRequest(1, RESULT_LIMIT);
    var artists = List.of(ArtistDtoFactory.withName("A"));

    // when
    underTest.collectUpcomingReleases(artists);

    // then
    verify(releaseService, times(1)).findReleases(any(), any(), eq(expectedPageRequest));
  }

  @Test
  @DisplayName("collecting upcoming releases returns list of releases")
  void test_upcoming_returns_releases() {
    // given
    var artists = List.of(ArtistDtoFactory.withName("A"));
    var releases = List.of(ReleaseDtoFactory.createDefault(), ReleaseDtoFactory.createDefault());
    doReturn(releases).when(releaseService).findReleases(any(), any(), any());

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

    // when
    underTest.collectRecentReleases(artists);

    // then
    verify(releaseService, times(1)).findReleases(eq(expectedArtistNames), any(), any());
  }

  @Test
  @DisplayName("collecting recent releases calls releaseService with correct time range")
  void test_recent_releases_calls_release_service_with_time_range() {
    // given
    var now = LocalDate.now();
    var expectedTimeRange = new TimeRange(now.minusMonths(TIME_RANGE_MONTHS), now);
    var artists = List.of(ArtistDtoFactory.withName("A"));

    // when
    underTest.collectRecentReleases(artists);

    // then
    verify(releaseService, times(1)).findReleases(any(), eq(expectedTimeRange), any());
  }

  @Test
  @DisplayName("collecting recent releases calls releaseService with correct page request")
  void test_recent_releases_calls_release_service_with_page_request() {
    // given
    var expectedPageRequest = new PageRequest(1, RESULT_LIMIT);
    var artists = List.of(ArtistDtoFactory.withName("A"));

    // when
    underTest.collectRecentReleases(artists);

    // then
    verify(releaseService, times(1)).findReleases(any(), any(), eq(expectedPageRequest));
  }

  @Test
  @DisplayName("collecting recent releases returns list of releases, sorted by descending release date")
  void test_recent_returns_releases() {
    // given
    var artists = List.of(ArtistDtoFactory.withName("A"));
    var now = LocalDate.now();
    var releases = Stream.of(ReleaseDtoFactory.withReleaseDate(now), ReleaseDtoFactory.withReleaseDate(now.plusDays(10))).collect(Collectors.toList());
    doReturn(new ArrayList<>(releases)).when(releaseService).findReleases(any(), any(), any());

    // when
    var result = underTest.collectRecentReleases(artists);

    // then
    assertThat(result).hasSize(releases.size());
    assertThat(result.get(0)).isEqualTo(releases.get(1));
    assertThat(result.get(1)).isEqualTo(releases.get(0));
  }
}
