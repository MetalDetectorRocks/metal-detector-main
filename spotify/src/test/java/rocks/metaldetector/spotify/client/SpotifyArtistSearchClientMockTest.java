package rocks.metaldetector.spotify.client;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SpotifyArtistSearchClientMockTest implements WithAssertions {

  private final SpotifyArtistSearchClientMock underTest = new SpotifyArtistSearchClientMock();

  @Test
  @DisplayName("should return mock result")
  void test_searchByName() {
    // when
    var result = underTest.searchByName("token", "query", 1, 10);

    // then
    assertThat(result.getArtists().getItems().size()).isEqualTo(1);
  }

  @Test
  @DisplayName("should return mock result")
  void test_searchById() {
    // when
    var result = underTest.searchById("token", "666");

    // then
    assertThat(result).isNotNull();
  }
}