package rocks.metaldetector.support;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;

@PropertySource(value = "classpath:application.yml")
@ConfigurationProperties(prefix = "security")
@Data
public class SecurityProperties {

  private String tokenIssuer;
  private String tokenSecret;
  private String rememberMeSecret;
  private long authTokenExpirationInMinutes;

}
