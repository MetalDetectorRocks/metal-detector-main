package rocks.metaldetector;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import rocks.metaldetector.testutil.WithIntegrationTestConfig;

@SpringBootTest
@ActiveProfiles("preview")
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:test-preview;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE"
})class MetalDetectorApplicationPreviewProfileIT implements WithIntegrationTestConfig {

  @Test
  void contextLoads() {
    // Simple test to check if the Spring Application context can be loaded successfully with 'preview' profile
  }
}
