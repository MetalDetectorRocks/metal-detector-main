package rocks.metaldetector.service.notification;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class NotificationConfigDtoTransformerTest implements WithAssertions {

  private final NotificationConfigDtoTransformer underTest = new NotificationConfigDtoTransformer();

  @Test
  @DisplayName("dto is transformed to entity")
  void test_transform() {
    // given
    var notificationConfigDto = NotificationConfigDto.builder().frequencyInWeeks(4)
        .notificationAtAnnouncementDate(true)
        .notificationAtReleaseDate(true)
        .notify(true)
        .build();

    // when
    var result = underTest.transform(notificationConfigDto);

    // then
    assertThat(result.getNotify()).isEqualTo(notificationConfigDto.isNotify());
    assertThat(result.getFrequencyInWeeks()).isEqualTo(notificationConfigDto.getFrequencyInWeeks());
    assertThat(result.getNotificationAtAnnouncementDate()).isEqualTo(notificationConfigDto.isNotificationAtAnnouncementDate());
    assertThat(result.getNotificationAtReleaseDate()).isEqualTo(notificationConfigDto.isNotificationAtReleaseDate());
  }
}
