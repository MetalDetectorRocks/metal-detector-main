package rocks.metaldetector.spotify.client;

import rocks.metaldetector.spotify.api.authentication.SpotifyUserAuthorizationResponse;

public interface SpotifyAuthenticationClient {

  String getAppAuthorizationToken();

  SpotifyUserAuthorizationResponse getUserAuthorizationToken(String code);
}
