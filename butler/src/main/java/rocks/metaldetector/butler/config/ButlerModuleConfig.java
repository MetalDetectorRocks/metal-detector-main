package rocks.metaldetector.butler.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ButlerModuleConfig {

  @Value("${metal.release.buter.unpaginated.releases.endpoint}")
  private String releasesEndpoint;

  @Bean
  public String releasesEndpoint() {
    return releasesEndpoint;
  }
}
