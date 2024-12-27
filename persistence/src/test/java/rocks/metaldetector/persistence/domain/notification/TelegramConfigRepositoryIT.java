package rocks.metaldetector.persistence.domain.notification;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import rocks.metaldetector.persistence.BaseDataJpaTest;
import rocks.metaldetector.persistence.domain.user.UserEntity;
import rocks.metaldetector.persistence.domain.user.UserFactory;
import rocks.metaldetector.persistence.domain.user.UserRepository;

import java.util.List;

import static rocks.metaldetector.persistence.domain.notification.NotificationChannel.EMAIL;
import static rocks.metaldetector.persistence.domain.notification.NotificationChannel.TELEGRAM;

class TelegramConfigRepositoryIT extends BaseDataJpaTest implements WithAssertions {

  private UserEntity user1;
  private UserEntity user2;
  private NotificationConfigEntity notificationConfig1;
  private TelegramConfigEntity telegramConfig;

  @Autowired
  private TelegramConfigRepository underTest;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private NotificationConfigRepository notificationConfigRepository;

  @BeforeEach
  void setup() {
    user1 = UserFactory.createUser("user1", "user1@example.com");
    user2 = UserFactory.createUser("user2", "user2@example.com");
    notificationConfig1 = NotificationConfigEntity.builder().user(user1).channel(TELEGRAM).build();
    NotificationConfigEntity notificationConfig2 = NotificationConfigEntity.builder().user(user1).channel(EMAIL).build();
    NotificationConfigEntity notificationConfig3 = NotificationConfigEntity.builder().user(user2).channel(EMAIL).build();
    telegramConfig = TelegramConfigEntity.builder().notificationConfig(notificationConfig1).registrationId(666).build();
    userRepository.save(user1);
    userRepository.save(user2);
    notificationConfigRepository.save(notificationConfig1);
    notificationConfigRepository.save(notificationConfig2);
    notificationConfigRepository.save(notificationConfig3);
    underTest.save(telegramConfig);
  }

  @AfterEach
  void tearDown() {
    underTest.deleteAll();
    notificationConfigRepository.deleteAll();
    userRepository.deleteAll(List.of(user1, user2));
  }

  @Test
  @DisplayName("findByUser finds correct telegram config for a given user")
  void test_find_by_user() {
    // when
    var resultOptional = underTest.findByUser(user1);

    // then
    assertThat(resultOptional).isPresent();
    assertThat(resultOptional.get()).isEqualTo(telegramConfig);
  }

  @Test
  @DisplayName("findByUser returns empty optional if no config exists for a given user")
  void test_find_by_user_returns_empty_optional() {
    // given
    var userWithoutConfig = UserFactory.createUser("userWithoutConfig", "userWithoutConfig@mail.mail");
    userRepository.save(userWithoutConfig);

    // when
    var resultOptional = underTest.findByUser(userWithoutConfig);

    // then
    assertThat(resultOptional).isEmpty();
  }

  @Test
  @DisplayName("should return true if registration id exists")
  void test_exists_true() {
    // given
    var registrationId = 666;

    // when
    var result = underTest.existsByRegistrationId(registrationId);

    // then
    assertThat(result).isTrue();
  }

  @Test
  @DisplayName("should return false if registration id does not exist")
  void test_exists_false() {
    // given
    var registrationId = 555;

    // when
    var result = underTest.existsByRegistrationId(registrationId);

    // then
    assertThat(result).isFalse();
  }

  @Test
  @DisplayName("should find telegram config entity by telegram registration id")
  void should_find_telegram_config_entity_by_registration_id() {
    // when
    var result = underTest.findByRegistrationId(telegramConfig.getRegistrationId());

    // then
    assertThat(result).isPresent();
    assertThat(result.get()).isEqualTo(telegramConfig);
  }

  @Test
  @DisplayName("should return empty Optional if no telegram config could be found")
  void should_return_empty_optional_for_unknown_registration_id() {
    // when
    var result = underTest.findByRegistrationId(555);

    // then
    assertThat(result).isNotPresent();
  }

  @Test
  @DisplayName("should find telegram config entity by notification config")
  void should_find_telegram_config_entity_by_notification_config() {
    // when
    var result = underTest.findByNotificationConfig(notificationConfig1);

    // then
    assertThat(result).isPresent();
    assertThat(result.get()).isEqualTo(telegramConfig);
  }

  @Test
  @DisplayName("should return empty Optional if no telegram config could be found")
  void should_return_empty_optional_for_unknown_notification_config() {
    // given
    var user = UserFactory.createUser("userWithoutTelegram", "userWithoutTelegram@mail.mail");
    var notificationConfigWithoutTelegram = NotificationConfigEntity.builder().user(user).channel(EMAIL).build();
    userRepository.save(user);
    notificationConfigRepository.save(notificationConfigWithoutTelegram);

    // when
    var result = underTest.findByNotificationConfig(notificationConfigWithoutTelegram);

    // then
    assertThat(result).isNotPresent();
  }

  @Test
  @DisplayName("should delete telegram config if exists")
  void test_delete_by_user() {
    // when
    underTest.deleteByUser(user1);
    var result = underTest.findByUser(user1);

    // then
    assertThat(result).isEmpty();
  }
}
