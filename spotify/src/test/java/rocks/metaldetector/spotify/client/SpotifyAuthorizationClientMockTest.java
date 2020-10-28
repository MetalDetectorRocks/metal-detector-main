package rocks.metaldetector.spotify.client;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static rocks.metaldetector.spotify.client.SpotifyAuthorizationClientMock.MOCK_TOKEN;

class SpotifyAuthorizationClientMockTest implements WithAssertions {

  private SpotifyAuthorizationClientMock underTest = new SpotifyAuthorizationClientMock();

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
    // when
    var result = underTest.getUserAuthorizationToken("code");

    // then
    assertThat(result).isNotNull();
  }

  @Test
  @DisplayName("getUserAuthenticationToken: should return mock response")
  void test_refresh_user_auth() {
    // when
    var result = underTest.refreshUserAuthorizationToken("refreshToken");

    // then
    assertThat(result).isNotNull();
  }
}