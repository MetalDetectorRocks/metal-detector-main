package rocks.metaldetector;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;
import rocks.metaldetector.testutil.BaseSpringBootTest;

@ActiveProfiles("preview")
class MetalDetectorApplicationPreviewProfileIT extends BaseSpringBootTest {

  @Test
  void contextLoads() {
    // Simple test to check if the Spring Application context can be loaded successfully with 'preview' profile
  }
}
