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

class NotificationConfigRepositoryIT extends BaseDataJpaTest implements WithAssertions {

  private static UserEntity USER_1;
  private static NotificationConfigEntity NOTIFICATION_CONFIG_1;
  private static NotificationConfigEntity NOTIFICATION_CONFIG_2;
  private static NotificationConfigEntity NOTIFICATION_CONFIG_3;

  @Autowired
  private NotificationConfigRepository underTest;

  @Autowired
  private UserRepository userRepository;

  @BeforeEach
  void setup() {
    USER_1 = UserFactory.createUser("user", "user@example.com");
    UserEntity USER_2 = UserFactory.createUser("user2", "user2@example.com");
    NOTIFICATION_CONFIG_1 = NotificationConfigEntity.builder()
        .user(USER_1)
        .notify(true)
        .notificationAtReleaseDate(true)
        .notificationAtAnnouncementDate(true)
        .frequencyInWeeks(4)
        .channel(EMAIL)
        .build();

    NOTIFICATION_CONFIG_2 = NotificationConfigEntity.builder()
        .user(USER_1)
        .notify(true)
        .notificationAtReleaseDate(true)
        .notificationAtAnnouncementDate(true)
        .frequencyInWeeks(4)
        .channel(TELEGRAM)
        .build();

    NOTIFICATION_CONFIG_3 = NotificationConfigEntity.builder()
        .user(USER_2)
        .notify(true)
        .notificationAtReleaseDate(true)
        .notificationAtAnnouncementDate(true)
        .frequencyInWeeks(4)
        .channel(EMAIL)
        .build();
    userRepository.save(USER_1);
    userRepository.save(USER_2);
    underTest.save(NOTIFICATION_CONFIG_1);
    underTest.save(NOTIFICATION_CONFIG_2);
    underTest.save(NOTIFICATION_CONFIG_3);
  }

  @AfterEach
  void tearDown() {
    underTest.deleteAll();
    userRepository.deleteAll();
  }

  @Test
  @DisplayName("should find all notification config entities for a given user")
  void should_find_all_notification_config_entities_by_user() {
    // when
    var result = underTest.findAllByUser(USER_1);

    // then
    assertThat(result).hasSize(2);
    assertThat(result.get(0)).isEqualTo(NOTIFICATION_CONFIG_1);
    assertThat(result.get(1)).isEqualTo(NOTIFICATION_CONFIG_2);
  }

  @Test
  @DisplayName("should delete notification config")
  void test_delete() {
    // when
    underTest.deleteAllByUser(USER_1);
    var result = underTest.findAllByUser(USER_1);

    // then
    assertThat(result).isEmpty();
  }

  @Test
  @DisplayName("findAllActive finds all configs with enabled user and notify = true")
  void test_find_all_active() {
    // given
    var deactivatedUser = UserFactory.createUser("deactivated", "deactivated@mail.mail");
    deactivatedUser.setEnabled(false);
    var deactivatedNotificationConfig = NotificationConfigEntity.builder().user(USER_1).notify(false).channel(EMAIL).build();
    var notificationConfigWithDeactivatedUser = NotificationConfigEntity.builder().user(deactivatedUser).notify(true).channel(EMAIL).build();
    userRepository.save(deactivatedUser);
    underTest.saveAll(List.of(deactivatedNotificationConfig, notificationConfigWithDeactivatedUser));

    // when
    var result = underTest.findAllActive();

    // then
    assertThat(result).hasSize(3);
    assertThat(result.get(0)).isEqualTo(NOTIFICATION_CONFIG_1);
    assertThat(result.get(1)).isEqualTo(NOTIFICATION_CONFIG_2);
    assertThat(result.get(2)).isEqualTo(NOTIFICATION_CONFIG_3);
  }
}
