package rocks.metaldetector.spotify.client;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SpotifyUserLibraryClientMockTest implements WithAssertions {

  private SpotifyUserLibraryClientMock underTest = new SpotifyUserLibraryClientMock();

  @Test
  @DisplayName("mock is not null")
  void test_result_not_null() {
    // when
    var result = underTest.fetchLikedAlbums("token", 666);

    // then
    assertThat(result).isNotNull();
  }
}