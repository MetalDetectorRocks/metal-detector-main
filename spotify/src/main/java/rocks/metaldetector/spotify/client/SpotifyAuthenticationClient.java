package rocks.metaldetector.spotify.client;

import rocks.metaldetector.spotify.api.authentication.SpotifyUserAuthorizationResponse;

public interface SpotifyAuthenticationClient {

  String getAppAuthenticationToken();

  SpotifyUserAuthorizationResponse getUserAuthorizationToken(String code);
}
