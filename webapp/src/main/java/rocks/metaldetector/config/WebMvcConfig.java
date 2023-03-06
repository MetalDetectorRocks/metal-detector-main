package rocks.metaldetector.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.FixedLocaleResolver;

import java.util.stream.Stream;

import static java.util.Locale.ENGLISH;
import static rocks.metaldetector.support.Endpoints.AntPattern.REST_ENDPOINTS;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

  @Value("${frontend.origin}")
  private String frontendOrigin;

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    String[] allowedOrigins = Stream.of(frontendOrigin, "http://localhost:3000", "http://localhost:1080").distinct().toArray(String[]::new);
    registry.addMapping(REST_ENDPOINTS)
        .allowedOrigins(allowedOrigins)
        .allowedMethods("*")
        .allowCredentials(true);
  }

  @Bean
  public LocaleResolver localeResolver() {
    return new FixedLocaleResolver(ENGLISH);
  }
}
