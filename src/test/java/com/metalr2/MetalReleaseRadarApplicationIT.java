package com.metalr2;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-integrationtest.properties")
@Tag("integration-test")
class MetalReleaseRadarApplicationIT {

  @Test
  void contextLoads() {
    // Simple test to check if the Spring Application context can be loaded successfully
  }

}
