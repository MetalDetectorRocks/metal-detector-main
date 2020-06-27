package rocks.metaldetector.config.misc;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(value = "classpath:application.yml")
@ConfigurationProperties(prefix = "application")
@Data
public class ApplicationProperties {

  private String host;

}
