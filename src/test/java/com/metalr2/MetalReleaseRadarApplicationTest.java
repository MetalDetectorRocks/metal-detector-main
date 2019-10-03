package com.metalr2;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-integrationtest.properties")
class MetalReleaseRadarApplicationTest {

  @Test
  void contextLoads() {
    // Simple test to check if the Spring Application context can be loaded successfully
  }

}
