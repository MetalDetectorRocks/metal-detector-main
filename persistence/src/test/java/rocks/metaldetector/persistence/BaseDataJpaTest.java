package rocks.metaldetector.persistence;

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ContextConfiguration;
import rocks.metaldetector.persistence.config.PersistenceModuleConfiguration;

import java.util.Optional;

@DataJpaTest(properties = "spring.main.allow-bean-definition-overriding=true")
@ContextConfiguration(classes = PersistenceModuleConfiguration.class)
public abstract class BaseDataJpaTest implements WithIntegrationTestConfig {

  protected static final String AUDITOR_USER = "ANONYMOUS";

  @EnableJpaAuditing
  @TestConfiguration
  static class MyTestConfiguration {

    @Bean
    public AuditorAware<String> auditorAware() {
      return () -> Optional.of(AUDITOR_USER);
    }
  }
}
