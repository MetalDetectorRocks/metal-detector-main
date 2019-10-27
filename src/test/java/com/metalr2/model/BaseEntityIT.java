package com.metalr2.model;

import org.assertj.core.api.WithAssertions;
import org.assertj.core.data.TemporalUnitLessThanOffset;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@DataJpaTest
@Tag("test")
class BaseEntityIT implements WithAssertions {

  private static final String AUDITOR_USER = "ANONYMOUS";

  @Autowired
  private SimpleTestRepository testRepository;

  @Test
  @DisplayName("All JPA auditing fields should have a valid value after persisting")
  void jpa_auditing_fields_should_be_not_null() {
    TemporalUnitLessThanOffset offset = new TemporalUnitLessThanOffset(500, ChronoUnit.MILLIS);
    LocalDateTime now = LocalDateTime.now();
    SimpleTestEntity testEntity = new SimpleTestEntity();

    assertThat(testEntity.getCreatedBy()).isNull();
    assertThat(testEntity.getCreatedDateTime()).isNull();
    assertThat(testEntity.getLastModifiedBy()).isNull();
    assertThat(testEntity.getLastModifiedDateTime()).isNull();

    testRepository.save(testEntity);

    assertThat(testEntity.getCreatedBy()).isEqualTo(AUDITOR_USER);
    assertThat(testEntity.getCreatedDateTime()).isCloseTo(now, offset);
    assertThat(testEntity.getLastModifiedBy()).isEqualTo(AUDITOR_USER);
    assertThat(testEntity.getLastModifiedDateTime()).isCloseTo(now, offset);
  }

  @EnableJpaAuditing
  @TestConfiguration
  static class MyTestConfiguration {

    @Bean
    public AuditorAware<String> auditorAware() {
      return () -> Optional.of(AUDITOR_USER);
    }

  }

}
