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
    var notificationConfigDto = NotificationConfigDto.builder().frequency(4)
        .notificationAtAnnouncementDate(true)
        .notificationAtReleaseDate(true)
        .build();

    // when
    var result = underTest.transform(notificationConfigDto);

    // then
    assertThat(result.getFrequency()).isEqualTo(notificationConfigDto.getFrequency());
    assertThat(result.getNotificationAtAnnouncementDate()).isEqualTo(notificationConfigDto.isNotificationAtAnnouncementDate());
    assertThat(result.getNotificationAtReleaseDate()).isEqualTo(notificationConfigDto.isNotificationAtReleaseDate());
  }
}