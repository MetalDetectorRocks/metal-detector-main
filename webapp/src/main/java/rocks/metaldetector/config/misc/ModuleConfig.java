package rocks.metaldetector.config.misc;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import rocks.metaldetector.butler.config.ButlerConfig;
import rocks.metaldetector.discogs.config.DiscogsConfig;
import rocks.metaldetector.persistence.config.PersistenceModuleConfiguration;
import rocks.metaldetector.spotify.config.SpotifyProperties;
import rocks.metaldetector.support.ApplicationProperties;
import rocks.metaldetector.support.SecurityProperties;
import rocks.metaldetector.telegram.config.TelegramProperties;

@Configuration
@Import(PersistenceModuleConfiguration.class)
@EnableConfigurationProperties(value = {SpotifyProperties.class, ApplicationProperties.class, TelegramProperties.class,
                                        ButlerConfig.class, DiscogsConfig.class, SecurityProperties.class})
public class ModuleConfig {
}
