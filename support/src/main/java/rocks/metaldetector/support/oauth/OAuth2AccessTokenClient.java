package rocks.metaldetector.support.oauth;

public interface OAuth2AccessTokenClient {

  String getAccessToken();
  void setRegistrationId(String registrationId);
}
