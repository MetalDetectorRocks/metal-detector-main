package rocks.metaldetector.config.misc;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@Getter
@PropertySource("classpath:application.yml")
public class MailProperties {

  private final String applicationHostUrl;
  private final String applicationPort;
  private final String fromEmail;

  public MailProperties(ApplicationProperties applicationProperties,
                        @Value("${server.port}") String applicationPort,
                        @Value("${spring.mail.properties.from}") String fromEmail) {
    this.fromEmail = fromEmail;
    this.applicationHostUrl = applicationProperties.getHost();
    this.applicationPort = applicationPort;
  }
}
