package rocks.metaldetector.config.misc;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.FixedLocaleResolver;

import static java.util.Locale.ENGLISH;

@Configuration
public class LocaleConfig {

  @Bean
  public LocaleResolver localeResolver() {
    return new FixedLocaleResolver(ENGLISH);
  }
}
