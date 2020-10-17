package rocks.metaldetector.service.spotify;

public interface SpotifyUserAuthorizationService {

  String prepareAuthorization();

  void fetchToken(String state, String code);
}
