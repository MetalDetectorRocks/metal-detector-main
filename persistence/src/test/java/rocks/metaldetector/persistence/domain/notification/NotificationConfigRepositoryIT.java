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

import static rocks.metaldetector.persistence.domain.notification.NotificationChannel.EMAIL;
import static rocks.metaldetector.persistence.domain.notification.NotificationChannel.TELEGRAM;

class NotificationConfigRepositoryIT extends BaseDataJpaTest implements WithAssertions {

  private static UserEntity USER_1;
  private static UserEntity USER_2;
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
    USER_2 = UserFactory.createUser("user2", "user2@example.com");
    NOTIFICATION_CONFIG_1 = NotificationConfigEntity.builder()
        .user(USER_1)
        .notificationAtReleaseDate(true)
        .notificationAtAnnouncementDate(true)
        .frequencyInWeeks(4)
        .channel(EMAIL)
        .build();

    NOTIFICATION_CONFIG_2 = NotificationConfigEntity.builder()
        .user(USER_1)
        .notificationAtReleaseDate(true)
        .notificationAtAnnouncementDate(true)
        .frequencyInWeeks(4)
        .channel(TELEGRAM)
        .build();

    NOTIFICATION_CONFIG_3 = NotificationConfigEntity.builder()
        .user(USER_2)
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
  @DisplayName("findByUserAndChannel returns entity if present")
  void test_find_by_user_and_channel_returns_entity() {
    // when
    var result = underTest.findByUserAndChannel(USER_1, EMAIL);

    // then
    assertThat(result).isPresent();
    assertThat(result.get()).isEqualTo(NOTIFICATION_CONFIG_1);
  }

  @Test
  @DisplayName("findByUserAndChannel returns empty optional")
  void test_find_by_user_and_channel() {
    // when
    var result = underTest.findByUserAndChannel(USER_2, TELEGRAM);

    // then
    assertThat(result).isEmpty();
  }

  @Test
  @DisplayName("should delete notification config by user")
  void test_delete_by_user() {
    // when
    underTest.deleteAllByUser(USER_1);
    var result = underTest.findAllByUser(USER_1);

    // then
    assertThat(result).isEmpty();
  }

  @Test
  @DisplayName("should delete notification config by user and channel")
  void test_delete_by_user_and_channel() {
    // when
    underTest.deleteByUserAndChannel(USER_1, TELEGRAM);
    var result = underTest.findAllByUser(USER_1);

    // then
    assertThat(result.size()).isEqualTo(1);
    assertThat(result.get(0).getChannel()).isNotEqualTo(TELEGRAM);
  }

  @Test
  @DisplayName("findAllActive finds all configs with enabled user")
  void test_find_all_active() {
    // given
    var deactivatedUser = UserFactory.createUser("deactivated", "deactivated@mail.mail");
    deactivatedUser.setEnabled(false);
    var notificationConfigWithDeactivatedUser = NotificationConfigEntity.builder().user(deactivatedUser).channel(EMAIL).build();
    userRepository.save(deactivatedUser);
    underTest.save(notificationConfigWithDeactivatedUser);

    // when
    var result = underTest.findAllActive();

    // then
    assertThat(result).hasSize(3);
    assertThat(result.get(0)).isEqualTo(NOTIFICATION_CONFIG_1);
    assertThat(result.get(1)).isEqualTo(NOTIFICATION_CONFIG_2);
    assertThat(result.get(2)).isEqualTo(NOTIFICATION_CONFIG_3);
  }
}
