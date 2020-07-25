package rocks.metaldetector.spotify.client;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static rocks.metaldetector.spotify.client.SpotifyAuthenticationClientMock.MOCK_TOKEN;

@ExtendWith(MockitoExtension.class)
class SpotifyAuthenticationClientMockTest implements WithAssertions {

  private SpotifyAuthenticationClientMock underTest = new SpotifyAuthenticationClientMock();

  @Test
  @DisplayName("Should return mock token string")
  void test() {
    // when
    var result = underTest.getAuthenticationToken();

    // then
    assertThat(result).isEqualTo(MOCK_TOKEN);
  }
}