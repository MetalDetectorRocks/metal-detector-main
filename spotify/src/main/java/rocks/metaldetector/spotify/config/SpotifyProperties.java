package rocks.metaldetector.spotify.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;

@PropertySource(value = "classpath:application.yml")
@ConfigurationProperties(prefix = "spotify")
@Data
public class SpotifyProperties {

  private String restBaseUrl;

  public SpotifyProperties(@Value("${rest-base-url}") String restBaseUrl) {
    this.restBaseUrl = restBaseUrl;
  }
}
