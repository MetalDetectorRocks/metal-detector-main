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

  @Autowired
  private NotificationConfigRepository underTest;

  @Autowired
  private UserRepository userRepository;

  private UserEntity user1;
  private UserEntity user2;
  private NotificationConfigEntity notificationConfig1;
  private NotificationConfigEntity notificationConfig2;
  private NotificationConfigEntity notificationConfig3;

  @BeforeEach
  void setup() {
    user1 = UserFactory.createUser("user", "user@example.com");
    user2 = UserFactory.createUser("user2", "user2@example.com");
    notificationConfig1 = NotificationConfigEntity.builder()
        .user(user1)
        .notificationAtReleaseDate(true)
        .notificationAtAnnouncementDate(true)
        .frequencyInWeeks(4)
        .channel(EMAIL)
        .build();

    notificationConfig2 = NotificationConfigEntity.builder()
        .user(user1)
        .notificationAtReleaseDate(true)
        .notificationAtAnnouncementDate(true)
        .frequencyInWeeks(4)
        .channel(TELEGRAM)
        .build();

    notificationConfig3 = NotificationConfigEntity.builder()
        .user(user2)
        .notificationAtReleaseDate(true)
        .notificationAtAnnouncementDate(true)
        .frequencyInWeeks(4)
        .channel(EMAIL)
        .build();

    userRepository.save(user1);
    userRepository.save(user2);
    underTest.save(notificationConfig1);
    underTest.save(notificationConfig2);
    underTest.save(notificationConfig3);
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
    var result = underTest.findAllByUser(user1);

    // then
    assertThat(result).hasSize(2);
    assertThat(result.get(0)).isEqualTo(notificationConfig1);
    assertThat(result.get(1)).isEqualTo(notificationConfig2);
  }

  @Test
  @DisplayName("findByUserAndChannel returns entity if present")
  void test_find_by_user_and_channel_returns_entity() {
    // when
    var result = underTest.findByUserAndChannel(user1, EMAIL);

    // then
    assertThat(result).isPresent();
    assertThat(result.get()).isEqualTo(notificationConfig1);
  }

  @Test
  @DisplayName("findByUserAndChannel returns empty optional")
  void test_find_by_user_and_channel() {
    // when
    var result = underTest.findByUserAndChannel(user2, TELEGRAM);

    // then
    assertThat(result).isEmpty();
  }

  @Test
  @DisplayName("should delete notification config by user")
  void test_delete_by_user() {
    // when
    underTest.deleteAllByUser(user1);
    var result = underTest.findAllByUser(user1);

    // then
    assertThat(result).isEmpty();
  }

  @Test
  @DisplayName("should delete all notification configs by users")
  void test_delete_by_users() {
    // when
    underTest.deleteAllByUserIn(List.of(user1, user2));
    var result = underTest.findAll();

    // then
    assertThat(result).isEmpty();
  }

  @Test
  @DisplayName("should delete notification config by user and channel")
  void test_delete_by_user_and_channel() {
    // when
    underTest.deleteByUserAndChannel(user1, TELEGRAM);
    var result = underTest.findAllByUser(user1);

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
    assertThat(result.get(0)).isEqualTo(notificationConfig1);
    assertThat(result.get(1)).isEqualTo(notificationConfig2);
    assertThat(result.get(2)).isEqualTo(notificationConfig3);
  }
}
