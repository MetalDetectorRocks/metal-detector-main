package rocks.metaldetector.persistence.domain.notification;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import rocks.metaldetector.persistence.BaseDataJpaTest;
import rocks.metaldetector.persistence.WithIntegrationTestConfig;
import rocks.metaldetector.persistence.domain.user.UserEntity;
import rocks.metaldetector.persistence.domain.user.UserFactory;
import rocks.metaldetector.persistence.domain.user.UserRepository;

class NotificationConfigRepositoryTest extends BaseDataJpaTest implements WithAssertions, WithIntegrationTestConfig {

  private static UserEntity USER;
  private static NotificationConfigEntity NOTIFICATION_CONFIG;

  @Autowired
  private NotificationConfigRepository underTest;

  @Autowired
  private UserRepository userRepository;

  @BeforeEach
  void setup() {
    USER = UserFactory.createUser("user", "user@example.com");
    NOTIFICATION_CONFIG = NotificationConfigEntity.builder()
        .user(USER)
        .notify(true)
        .notificationAtReleaseDate(true)
        .notificationAtAnnouncementDate(true)
        .frequencyInWeeks(4)
        .telegramRegistrationId(666)
        .build();
    userRepository.save(USER);
    underTest.save(NOTIFICATION_CONFIG);
  }

  @AfterEach
  void tearDown() {
    underTest.deleteAll();
    userRepository.deleteAll();
  }

  @Test
  @DisplayName("should find notification config entity by user id")
  void should_find_notification_config_entity_by_user_id() {
    // when
    var result = underTest.findByUserId(USER.getId());

    // then
    assertThat(result).isPresent();
    assertThat(result.get()).isEqualTo(NOTIFICATION_CONFIG);
  }

  @Test
  @DisplayName("should return empty Optional if no notification config could be found")
  void should_return_empty_optional_for_unknown_user_id() {
    // when
    var result = underTest.findByUserId(123456L);

    // then
    assertThat(result).isNotPresent();
  }

  @Test
  @DisplayName("should delete notification config")
  void test_delete() {
    // when
    underTest.deleteByUserId(USER.getId());
    var result = underTest.findByUserId(USER.getId());

    // then
    assertThat(result).isNotPresent();
  }

  @Test
  @DisplayName("should return true if notification config exists")
  void test_exists_true() {
    // given
    var registrationId = 666;

    // when
    var result = underTest.existsByTelegramRegistrationId(registrationId);

    // then
    assertThat(result).isTrue();
  }

  @Test
  @DisplayName("should return false if notification config does not exist")
  void test_exists_false() {
    // given
    var registrationId = 555;

    // when
    var result = underTest.existsByTelegramRegistrationId(registrationId);

    // then
    assertThat(result).isFalse();
  }

  @Test
  @DisplayName("should find notification config entity by telegram registration id")
  void should_find_notification_config_entity_by_registration_id() {
    // when
    var result = underTest.findByTelegramRegistrationId(NOTIFICATION_CONFIG.getTelegramRegistrationId());

    // then
    assertThat(result).isPresent();
    assertThat(result.get()).isEqualTo(NOTIFICATION_CONFIG);
  }

  @Test
  @DisplayName("should return empty Optional if no notification config could be found")
  void should_return_empty_optional_for_unknown_registration_id() {
    // when
    var result = underTest.findByTelegramRegistrationId(555);

    // then
    assertThat(result).isNotPresent();
  }
}
