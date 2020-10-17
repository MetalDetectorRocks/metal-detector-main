package rocks.metaldetector.spotify.client.transformer;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import rocks.metaldetector.spotify.client.SpotifyDtoFactory.SpotfiyUserAuthorizationResponseFactory;

class SpotifyUserAuthorizationTransformerTest implements WithAssertions {

  private final SpotifyUserAuthorizationTransformer underTest = new SpotifyUserAuthorizationTransformer();

  @Test
  @DisplayName("SpotifyUserAuthorizationResponse is transformed to dto")
  void test_response_is_transformed() {
    // given
    var authorizationResponse = SpotfiyUserAuthorizationResponseFactory.createDefault();

    // when
    var result = underTest.transform(authorizationResponse);

    // then
    assertThat(result.getAccessToken()).isEqualTo(authorizationResponse.getAccessToken());
    assertThat(result.getRefreshToken()).isEqualTo(authorizationResponse.getRefreshToken());
    assertThat(result.getExpiresIn()).isEqualTo(authorizationResponse.getExpiresIn());
    assertThat(result.getScope()).isEqualTo(authorizationResponse.getScope());
    assertThat(result.getTokenType()).isEqualTo(authorizationResponse.getTokenType());
  }
}
