package com.metalr2.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class AbstractEntityTest {

  @Autowired
  private SimpleTestRepository testRepository;

  @EnableJpaAuditing
  @TestConfiguration
  static class MyTestConfiguration {

    @Bean
    public AuditorAware<String> auditorAware() {
      return () -> Optional.of("ANONYMOUS");
    }

  }

  @Test
  void jpaAuditingFieldsShouldBeNotNull() {
    SimpleTestEntity testEntity = new SimpleTestEntity();

    assertNull(testEntity.getCreatedBy());
    assertNull(testEntity.getCreatedDateTime());
    assertNull(testEntity.getLastModifiedBy());
    assertNull(testEntity.getLastModifiedDateTime());

    testRepository.save(testEntity);

    assertEquals("ANONYMOUS", testEntity.getCreatedBy());
    assertTrue(LocalDateTime.now().isAfter(testEntity.getCreatedDateTime()));
    assertEquals("ANONYMOUS", testEntity.getLastModifiedBy());
    assertTrue(LocalDateTime.now().isAfter(testEntity.getLastModifiedDateTime()));
  }

}