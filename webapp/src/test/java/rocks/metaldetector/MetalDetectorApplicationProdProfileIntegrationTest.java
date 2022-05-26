package rocks.metaldetector;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import rocks.metaldetector.testutil.BaseSpringBootTest;

@ActiveProfiles("prod")
@SpringBootTest
class MetalDetectorApplicationProdProfileIntegrationTest extends BaseSpringBootTest {

  @Test
  void contextLoads() {
    // Simple test to check if the Spring Application context can be loaded successfully with 'prod' profile
  }
}
