package rocks.metaldetector;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import rocks.metaldetector.testutil.WithIntegrationTestProfile;

@SpringBootTest
class MetalDetectorApplicationIT implements WithIntegrationTestProfile {

  @Test
  void contextLoads() {
    // Simple test to check if the Spring Application context can be loaded successfully
  }

}
