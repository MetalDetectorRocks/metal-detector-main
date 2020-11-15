package rocks.metaldetector.service.spotify;

public interface SpotifyUserAuthorizationService {

  String prepareAuthorization();

  void persistInitialToken(String state, String code);

  String getOrRefreshToken();
}
