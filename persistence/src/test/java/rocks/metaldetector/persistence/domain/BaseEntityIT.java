package rocks.metaldetector.persistence.domain;

import org.assertj.core.api.WithAssertions;
import org.assertj.core.data.TemporalUnitLessThanOffset;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import rocks.metaldetector.persistence.BaseDataJpaTest;
import rocks.metaldetector.persistence.WithIntegrationTestConfig;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

class BaseEntityIT extends BaseDataJpaTest implements WithAssertions, WithIntegrationTestConfig {

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
}
