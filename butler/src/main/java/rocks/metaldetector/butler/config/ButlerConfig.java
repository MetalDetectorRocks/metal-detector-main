package rocks.metaldetector.butler.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;

@PropertySource(value = "classpath:application.yml")
@ConfigurationProperties(prefix = "metal-release-butler")
@Data
public class ButlerConfig {

  private String host;
  private String releasesUrl;
  private String unpaginatedReleasesUrl;
  private String importUrl;
  private String retryCoverDownloadUrl;
  private String statisticsUrl;

}
