package rocks.metaldetector.spotify.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import rocks.metaldetector.support.ApplicationProperties;

import java.util.Arrays;
import java.util.List;

@Component
@PropertySource(value = "classpath:application.yml")
@ConfigurationProperties(prefix = "spotify")
@Data
public class SpotifyProperties {

  private Environment environment;
  private String restBaseUrl;
  private String applicationHostUrl;
  private int applicationPort;

  public SpotifyProperties(Environment environment, ApplicationProperties applicationProperties,
                           @Value("${rest-base-url}") String restBaseUrl) {
    this.environment = environment;
    this.applicationHostUrl = applicationProperties.getHost();
    this.applicationPort = applicationProperties.getPort();
    this.restBaseUrl = restBaseUrl;
  }

  public String getApplicationHostUrl() {
    List<String> profiles = Arrays.asList(environment.getActiveProfiles());
    if (profiles.contains("preview") || profiles.contains("prod")) {
      return this.applicationHostUrl;
    }
    return this.applicationHostUrl + ":" + this.applicationPort;
  }
}
