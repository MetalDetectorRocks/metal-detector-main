package rocks.metaldetector.spotify.client;

import rocks.metaldetector.spotify.api.authorization.SpotifyUserAuthorizationResponse;

public interface SpotifyAuthorizationClient {

  SpotifyUserAuthorizationResponse getUserAuthorizationToken(String code);

  SpotifyUserAuthorizationResponse refreshUserAuthorizationToken(String refreshToken);
}
