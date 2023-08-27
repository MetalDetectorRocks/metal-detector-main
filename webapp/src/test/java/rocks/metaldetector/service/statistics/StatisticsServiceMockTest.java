package rocks.metaldetector.service.statistics;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class StatisticsServiceMockTest implements WithAssertions {

  StatisticsServiceMock underTest = new StatisticsServiceMock();

  @Test
  @DisplayName("response with mock results is created")
  void test_response() {
    // when
    var result = underTest.createStatisticsResponse();

    // then
    assertThat(result).isNotNull();
    assertThat(result.getUserInfo()).isNotNull();
    assertThat(result.getArtistFollowingInfo()).isNotNull();
    assertThat(result.getReleaseInfo()).isNotNull();
    assertThat(result.getImportInfo()).isNotEmpty();
  }
}
