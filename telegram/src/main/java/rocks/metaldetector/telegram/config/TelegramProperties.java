package rocks.metaldetector.telegram.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource(value = "classpath:application.yml")
@ConfigurationProperties(prefix = "telegram")
@Data
public class TelegramProperties {

  private final String restBaseUrl;
  private final String botId;

  public TelegramProperties(@Value("${rest-base-url}") String restBaseUrl,
                            @Value("${bot-id}") String botId) {
    this.restBaseUrl = restBaseUrl;
    this.botId = botId;
  }
}
