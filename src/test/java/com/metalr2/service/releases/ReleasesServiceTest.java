package com.metalr2.service.releases;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import static org.mockito.Mockito.reset;

@ExtendWith(MockitoExtension.class)
class ReleasesServiceTest implements WithAssertions {

  @Mock
  private RestTemplate restTemplate;

  @InjectMocks
  private ReleasesServiceImpl releasesService;

  @BeforeEach
  void setUp() {
  }

  @AfterEach
  void tearDown() {
    reset(restTemplate);
  }

  @Test
  @DisplayName("getReleases() should return valid result")
  void get_releases() {
    // given
//    when(restTemplate.postForEntity())
  }
}