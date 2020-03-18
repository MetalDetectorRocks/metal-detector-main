package rocks.metaldetector.discogs;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:application.properties")
@ConfigurationProperties(prefix = "discogs")
@Data
public class DiscogsConfig {

  private String userAgent;
  private String accessToken;
  private String restBaseUrl;

}
