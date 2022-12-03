package rocks.metaldetector.config.misc;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextListener;

@Configuration
@EnableCaching
public class CacheConfig {

  @Bean
  @ConditionalOnMissingBean(RequestContextListener.class)
  public RequestContextListener requestContextListener() {
    return new RequestContextListener();
  }
}
