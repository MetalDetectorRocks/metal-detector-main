package rocks.metaldetector.spotify.client;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import rocks.metaldetector.spotify.api.authorization.SpotifyUserAuthorizationResponse;

@Slf4j
@Service
@Profile("mockmode")
@AllArgsConstructor
public class SpotifyAuthorizationClientMock implements SpotifyAuthorizationClient {

  static final String MOCK_TOKEN = "i'm a token";

  @Override
  public SpotifyUserAuthorizationResponse getUserAuthorizationToken(String code) {
    return SpotifyUserAuthorizationResponse.builder()
        .accessToken(MOCK_TOKEN)
        .refreshToken(MOCK_TOKEN)
        .tokenType("type")
        .expiresIn(3600)
        .scope("everything")
        .build();
  }

  @Override
  public SpotifyUserAuthorizationResponse refreshUserAuthorizationToken(String refreshToken) {
    return SpotifyUserAuthorizationResponse.builder()
        .accessToken(MOCK_TOKEN)
        .tokenType("type")
        .expiresIn(3600)
        .scope("everything")
        .build();
  }
}
