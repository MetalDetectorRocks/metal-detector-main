package rocks.metaldetector.config.logging;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

@Configuration
public class LoggingConfig {

  @Bean
  public CommonsRequestLoggingFilter restLoggingFilter() {
    return new RestRequestLoggingFilter();
  }
}
