package rocks.metaldetector.discogs.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(value = "classpath:application.yml")
@ConfigurationProperties(prefix = "discogs")
@Data
public class DiscogsConfig {

  private String userAgent;
  private String accessToken;
  private String restBaseUrl;

}
