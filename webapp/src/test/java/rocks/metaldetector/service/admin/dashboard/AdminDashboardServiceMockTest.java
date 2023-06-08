package rocks.metaldetector.service.admin.dashboard;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AdminDashboardServiceMockTest implements WithAssertions {

  AdminDashboardServiceMock underTest = new AdminDashboardServiceMock();

  @Test
  @DisplayName("response with mock results is created")
  void test_response() {
    // when
    var result = underTest.createAdminDashboardResponse();

    // then
    assertThat(result).isNotNull();
    assertThat(result.getUserInfo()).isNotNull();
    assertThat(result.getArtistFollowingInfo()).isNotNull();
    assertThat(result.getReleaseInfo()).isNotNull();
    assertThat(result.getImportInfo()).isNotEmpty();
  }
}
