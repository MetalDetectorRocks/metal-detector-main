package rocks.metaldetector.service.dashboard;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import rocks.metaldetector.support.TimeRange;

import java.time.LocalDate;

class DashboardServiceMockTest implements WithAssertions {

  DashboardServiceMock underTest = new DashboardServiceMock();

  @Test
  @DisplayName("homepageResponse with mock results is created")
  void test_response() {
    // when
    var result = underTest.createDashboardResponse();

    // then
    assertThat(result).isNotNull();
    assertThat(result.getUpcomingReleases()).isNotEmpty();
    assertThat(result.getRecentReleases()).isNotEmpty();
    assertThat(result.getMostExpectedReleases()).isNotEmpty();
    assertThat(result.getRecentlyFollowedArtists()).isNotEmpty();
    assertThat(result.getFavoriteCommunityArtists()).isNotEmpty();
  }

  @Test
  @DisplayName("release is returned for top releases")
  void test_top_releases() {
    // given
    var timeRange = new TimeRange(LocalDate.now(), null);

    // when
    var result = underTest.findTopReleases(timeRange, 1, 10);

    // then
    assertThat(result).isNotNull().isNotEmpty();
  }
}
