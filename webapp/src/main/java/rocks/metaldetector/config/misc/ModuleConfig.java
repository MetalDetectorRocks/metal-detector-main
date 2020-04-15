package rocks.metaldetector.config.misc;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import rocks.metaldetector.persistence.config.PersistenceModuleConfiguration;

@Configuration
@Import(PersistenceModuleConfiguration.class)
public class ModuleConfig {
}
