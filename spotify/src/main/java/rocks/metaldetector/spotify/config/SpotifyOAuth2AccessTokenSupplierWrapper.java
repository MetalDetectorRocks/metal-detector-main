package rocks.metaldetector.spotify.config;

import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import rocks.metaldetector.support.oauth.OAuth2AccessTokenSupplier;

@Component
@AllArgsConstructor
public class SpotifyOAuth2AccessTokenSupplierWrapper implements OAuth2AccessTokenSupplier {

  private final OAuth2AccessTokenSupplier oAuth2AccessTokenSupplier;

  @Override
  @Cacheable("spotifyAppAuthorizationToken")
  public String get() {
    return oAuth2AccessTokenSupplier.get();
  }

  @Override
  public void setRegistrationId(String registrationId) {
    oAuth2AccessTokenSupplier.setRegistrationId(registrationId);
  }
}
