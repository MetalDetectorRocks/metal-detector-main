package rocks.metaldetector.service.notification;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import rocks.metaldetector.persistence.domain.notification.NotificationConfigEntity;

class NotificationConfigTransformerTest implements WithAssertions {

  private final NotificationConfigTransformer underTest = new NotificationConfigTransformer();

  @Test
  @DisplayName("entity is transformed to dto")
  void test_transform() {
    // given
    var notificationConfigEntity = NotificationConfigEntity.builder().frequencyInWeeks(4)
        .notificationAtAnnouncementDate(true)
        .notificationAtReleaseDate(true)
        .notify(true)
        .telegramChatId(100)
        .build();

    // when
    var result = underTest.transform(notificationConfigEntity);

    // then
    assertThat(result.isNotify()).isEqualTo(notificationConfigEntity.getNotify());
    assertThat(result.getFrequencyInWeeks()).isEqualTo(notificationConfigEntity.getFrequencyInWeeks());
    assertThat(result.isNotificationAtAnnouncementDate()).isEqualTo(notificationConfigEntity.getNotificationAtAnnouncementDate());
    assertThat(result.isNotificationAtReleaseDate()).isEqualTo(notificationConfigEntity.getNotificationAtReleaseDate());
    assertThat(result.isTelegramNotificationsActive()).isTrue();
  }
}
