package rocks.metaldetector.spotify.client;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Profile("mockmode")
@AllArgsConstructor
public class SpotifyAuthenticationClientMock implements SpotifyAuthenticationClient {

  static final String MOCK_TOKEN = "i'm a token";

  @Override
  public String getAuthenticationToken() {
    return MOCK_TOKEN;
  }
}
