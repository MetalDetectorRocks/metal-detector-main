package rocks.metaldetector.support;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(value = "classpath:application.yml")
@Data
public class ApplicationProperties {

  private final String host;
  private final int port;

  public ApplicationProperties(@Value("${application.host}") String host,
                               @Value("${server.port}") int port) {
    this.host = host;
    this.port = port;
  }
}
