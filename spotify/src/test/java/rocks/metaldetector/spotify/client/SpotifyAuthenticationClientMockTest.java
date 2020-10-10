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
  @DisplayName("getAppAuthenticationToken: should return mock token string")
  void test_get_app_token() {
    // when
    var result = underTest.getAppAuthorizationToken();

    // then
    assertThat(result).isEqualTo(MOCK_TOKEN);
  }

  @Test
  @DisplayName("getUserAuthenticationToken: should return mock response")
  void test_get_user_auth_response() {
    // given
    var code = "code";

    // when
    var result = underTest.getUserAuthorizationToken(code);

    // then
    assertThat(result).isNotNull();
  }
}