package rocks.metaldetector.butler.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(value = "classpath:application.yml")
@ConfigurationProperties(prefix = "metal-release-butler")
@Data
public class ButlerConfig {

  private String accessToken;
  private String unpaginatedReleasesUrl;
  private String importUrl;

}
