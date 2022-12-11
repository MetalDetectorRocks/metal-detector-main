package rocks.metaldetector.support;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;

@PropertySource(value = "classpath:application.yml")
@ConfigurationProperties(prefix = "security")
@Data
public class SecurityProperties {

  private String jwtIssuer;
  private String jwtSecret;
  private long accessTokenExpirationInMin;
  private long refreshTokenExpirationInMin;
  private boolean secureCookie;
}
