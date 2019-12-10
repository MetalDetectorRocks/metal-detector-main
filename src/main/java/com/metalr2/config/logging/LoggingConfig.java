package com.metalr2.config.logging;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

@Configuration
public class LoggingConfig {

  @Bean
  public CommonsRequestLoggingFilter logFilter() {
    return new RestRequestLoggingFilter();
  }

}
