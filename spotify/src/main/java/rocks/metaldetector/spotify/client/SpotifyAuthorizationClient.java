package rocks.metaldetector.spotify.client;

import rocks.metaldetector.spotify.api.authorization.SpotifyUserAuthorizationResponse;

public interface SpotifyAuthorizationClient {

  String getAppAuthorizationToken();

  SpotifyUserAuthorizationResponse getUserAuthorizationToken(String code);
}
