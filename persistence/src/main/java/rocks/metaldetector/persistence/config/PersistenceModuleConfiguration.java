package rocks.metaldetector.persistence.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import rocks.metaldetector.persistence.domain.user.AbstractUserEntity;

import java.util.Optional;

@Configuration
@EnableJpaAuditing
@EnableJpaRepositories(basePackages = {"rocks.metaldetector.persistence"})
@EntityScan(basePackages = {"rocks.metaldetector.persistence"})
@ComponentScan(basePackages = {"rocks.metaldetector.persistence"})
public class PersistenceModuleConfiguration {

  private static final String ANONYMOUS_USERNAME = "ANONYMOUS";

  @Bean
  public AuditorAware<String> auditorAware() {
    return () -> {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      if (authentication != null) {
        Object principal = authentication.getPrincipal();
        if (principal instanceof AbstractUserEntity) {
          String username = ((AbstractUserEntity) principal).getUsername();
          return Optional.of(username != null ? username : ANONYMOUS_USERNAME);
        }
      }

      return Optional.of(ANONYMOUS_USERNAME);
    };
  }
}
