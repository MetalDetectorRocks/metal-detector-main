package rocks.metaldetector.telegram.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;

@PropertySource(value = "classpath:application.yml")
@ConfigurationProperties(prefix = "telegram")
@Data
public class TelegramProperties {

  private String restBaseUrl;
  private String botId;

}
