package com.metalr2.config.misc;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:discogs.properties")
@ConfigurationProperties(prefix = "discogs")
@Data
public class DiscogsConfig {

  private String userAgent;
  private String accessToken;
  private String restBaseUrl;

}
