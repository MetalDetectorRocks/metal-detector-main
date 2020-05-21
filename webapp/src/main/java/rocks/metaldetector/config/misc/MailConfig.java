package rocks.metaldetector.config.misc;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@Getter
@PropertySource("classpath:application.yml")
public class MailConfig {

  private final String fromEmail;
  private final String applicationHostUrl;
  private final String applicationPort;

  public MailConfig(@Value("${spring.mail.properties.from}") String fromEmail,
                    @Value("${application.host}") String applicationHostUrl,
                    @Value("${server.port}") String applicationPort) {
    this.fromEmail = fromEmail;
    this.applicationHostUrl = applicationHostUrl;
    this.applicationPort = applicationPort;
  }
}
