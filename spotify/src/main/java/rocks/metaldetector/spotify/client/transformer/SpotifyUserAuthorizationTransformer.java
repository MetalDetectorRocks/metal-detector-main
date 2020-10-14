package rocks.metaldetector.spotify.client.transformer;

import org.springframework.stereotype.Component;
import rocks.metaldetector.spotify.api.authorization.SpotifyUserAuthorizationResponse;
import rocks.metaldetector.spotify.facade.dto.SpotifyUserAuthorizationDto;

@Component
public class SpotifyUserAuthorizationTransformer {

  public SpotifyUserAuthorizationDto transform(SpotifyUserAuthorizationResponse response) {
    return SpotifyUserAuthorizationDto.builder()
        .accessToken(response.getAccessToken())
        .refreshToken(response.getRefreshToken())
        .expiresIn(response.getExpiresIn())
        .tokenType(response.getTokenType())
        .scope(response.getScope())
        .build();
  }
}
