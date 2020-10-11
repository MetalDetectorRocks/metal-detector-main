package rocks.metaldetector.persistence;

import org.junit.jupiter.api.Tag;
import org.springframework.test.context.TestPropertySource;

@Tag("integration-test")
@TestPropertySource(locations = "classpath:integrationtest.properties")
public interface WithIntegrationTestConfig {
}
