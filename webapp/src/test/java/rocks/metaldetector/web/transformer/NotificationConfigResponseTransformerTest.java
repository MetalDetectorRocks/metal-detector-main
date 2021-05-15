package rocks.metaldetector.web.transformer;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import rocks.metaldetector.service.notification.config.NotificationConfigDto;
import rocks.metaldetector.service.notification.config.TelegramConfigDto;
import rocks.metaldetector.web.api.request.UpdateNotificationConfigRequest;
import rocks.metaldetector.web.api.response.EmailConfig;
import rocks.metaldetector.web.api.response.TelegramConfig;

import java.util.List;

import static rocks.metaldetector.persistence.domain.notification.NotificationChannel.EMAIL;
import static rocks.metaldetector.persistence.domain.notification.NotificationChannel.TELEGRAM;

class NotificationConfigResponseTransformerTest implements WithAssertions {

  private final NotificationConfigResponseTransformer underTest = new NotificationConfigResponseTransformer();

  @Test
  @DisplayName("email config is transformed")
  void test_email_config_transformed() {
    // given
    var notificationConfigDto = NotificationConfigDto.builder()
        .channel(EMAIL.name())
        .frequencyInWeeks(4)
        .notificationAtAnnouncementDate(true)
        .notificationAtReleaseDate(true)
        .notify(true)
        .build();
    var expectedEmailConfig = EmailConfig.builder()
        .notify(true)
        .frequencyInWeeks(4)
        .notificationAtAnnouncementDate(true)
        .notificationAtReleaseDate(true)
        .build();

    // when
    var result = underTest.transformResponse(List.of(notificationConfigDto), null);

    // then
    assertThat(result.getEmailConfig()).isEqualTo(expectedEmailConfig);
  }

  @Test
  @DisplayName("telegram config is transformed")
  void test_telegram_config_transformed() {
    // given
    var notificationConfigDto = NotificationConfigDto.builder()
        .channel(TELEGRAM.name())
        .frequencyInWeeks(4)
        .notificationAtAnnouncementDate(true)
        .notificationAtReleaseDate(true)
        .notify(true)
        .build();
    var telegramConfigDto = TelegramConfigDto.builder()
        .registrationId(666)
        .chatId(555)
        .build();
    var expectedTelegramConfig = TelegramConfig.builder()
        .notify(true)
        .frequencyInWeeks(4)
        .notificationAtAnnouncementDate(true)
        .notificationAtReleaseDate(true)
        .registrationId(666)
        .notificationsActivated(true)
        .build();

    // when
    var result = underTest.transformResponse(List.of(notificationConfigDto), telegramConfigDto);

    // then
    assertThat(result.getTelegramConfig()).isEqualTo(expectedTelegramConfig);
  }

  @Test
  @DisplayName("update request is transformed")
  void test_update_request() {
    // given
    var request = UpdateNotificationConfigRequest.builder()
        .channel("channel")
        .notificationAtReleaseDate(true)
        .notificationAtAnnouncementDate(true)
        .notify(true)
        .frequencyInWeeks(4)
        .build();
    var expectedDto = NotificationConfigDto.builder()
        .channel("channel")
        .notify(true)
        .notificationAtReleaseDate(true)
        .notificationAtAnnouncementDate(true)
        .frequencyInWeeks(4)
        .build();

    // when
    var result = underTest.transformUpdateRequest(request);

    // then
    assertThat(result).isEqualTo(expectedDto);
  }
}
