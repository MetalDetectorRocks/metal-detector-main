package rocks.metaldetector.config.misc;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import rocks.metaldetector.support.ApplicationProperties;

@Component
@Getter
@PropertySource("classpath:application.yml")
public class MailProperties {

  private final String applicationHostUrl;
  private final int applicationPort;
  private final String fromEmail;
  private final String fromName;

  public MailProperties(ApplicationProperties applicationProperties,
                        @Value("${spring.mail.properties.from}") String fromEmail) {
    this.fromEmail = fromEmail;
    this.applicationHostUrl = applicationProperties.getHost();
    this.applicationPort = applicationProperties.getPort();
    this.fromName = applicationProperties.getName();
  }
}
