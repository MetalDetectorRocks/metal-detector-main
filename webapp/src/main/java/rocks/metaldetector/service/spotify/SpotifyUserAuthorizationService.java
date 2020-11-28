package rocks.metaldetector.service.spotify;

public interface SpotifyUserAuthorizationService {

  boolean exists();

  String prepareAuthorization();

  void persistInitialToken(String state, String code);

  String getOrRefreshToken();
}
