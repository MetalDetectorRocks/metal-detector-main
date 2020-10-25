package rocks.metaldetector.spotify.client;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SpotifyImportClientMockTest implements WithAssertions {

  private SpotifyImportClientMock underTest = new SpotifyImportClientMock();

  @Test
  @DisplayName("mock is not null")
  void test_result_not_null() {
    // when
    var result = underTest.importAlbums("token", 666);

    // then
    assertThat(result).isNotNull();
  }
}