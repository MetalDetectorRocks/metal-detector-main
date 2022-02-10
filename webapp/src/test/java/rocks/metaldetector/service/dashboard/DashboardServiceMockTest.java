package rocks.metaldetector.service.dashboard;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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
}
