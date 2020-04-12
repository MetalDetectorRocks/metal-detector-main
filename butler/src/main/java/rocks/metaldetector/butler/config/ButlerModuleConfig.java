package rocks.metaldetector.butler.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ButlerModuleConfig {

  @Value("${metal-release-butler.unpaginated-releases-url}")
  private String releasesEndpoint;

  @Value("${metal-release-butler.import-url}")
  private String importEndpoint;

  @Bean
  public String releasesEndpoint() {
    return releasesEndpoint;
  }

  @Bean
  public String importEndpoint() {
    return importEndpoint;
  }
}
