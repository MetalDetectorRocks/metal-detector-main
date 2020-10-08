package rocks.metaldetector.spotify.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import rocks.metaldetector.support.ApplicationProperties;

import java.util.Arrays;
import java.util.List;

@Configuration
@PropertySource(value = "classpath:application.yml")
@ConfigurationProperties(prefix = "spotify")
@Getter
public class SpotifyProperties {

  private final Environment environment;
  private final String clientId;
  private final String clientSecret;
  private final String restBaseUrl;
  private final String authenticationBaseUrl;
  private final String applicationHostUrl;
  private final int applicationPort;

  public SpotifyProperties(Environment environment, ApplicationProperties applicationProperties,
                           @Value("${client-id}") String clientId,
                           @Value("${client-secret}") String clientSecret,
                           @Value("${rest-base-url}") String restBaseUrl,
                           @Value("${authentication-base-url}") String authenticationBaseUrl) {
    this.environment = environment;
    this.applicationHostUrl = applicationProperties.getHost();
    this.applicationPort = applicationProperties.getPort();
    this.clientId = clientId;
    this.clientSecret = clientSecret;
    this.restBaseUrl = restBaseUrl;
    this.authenticationBaseUrl = authenticationBaseUrl;
  }

  public String getApplicationHostUrl() {
    List<String> profiles = Arrays.asList(environment.getActiveProfiles());
    if (profiles.contains("preview") || profiles.contains("prod")) {
      return this.applicationHostUrl;
    }
    return this.applicationHostUrl + ":" + this.applicationPort;
  }
}
