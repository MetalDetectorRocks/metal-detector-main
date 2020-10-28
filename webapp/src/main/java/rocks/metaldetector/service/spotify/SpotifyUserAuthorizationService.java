package rocks.metaldetector.service.spotify;

public interface SpotifyUserAuthorizationService {

  String prepareAuthorization();

  void fetchInitialToken(String state, String code);

  String getOrRefreshToken();
}
