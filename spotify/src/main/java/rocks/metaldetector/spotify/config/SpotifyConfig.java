package rocks.metaldetector.spotify.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(value = "classpath:application.yml")
@ConfigurationProperties(prefix = "spotify")
@Data
public class SpotifyConfig {

  private String clientId;
  private String clientSecret;
  private String restBaseUrl;
  private String authenticationBaseUrl;
  @Value("${application.host}")
  private String host;
  @Value("${server.port}")
  private int port;

}
