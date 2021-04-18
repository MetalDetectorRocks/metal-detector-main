package rocks.metaldetector;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import rocks.metaldetector.testutil.WithIntegrationTestConfig;

@SpringBootTest
@ActiveProfiles("preview")
class MetalDetectorApplicationPreviewProfileIT implements WithIntegrationTestConfig {

  @Test
  void contextLoads() {
    // Simple test to check if the Spring Application context can be loaded successfully with 'preview' profile
  }
}
