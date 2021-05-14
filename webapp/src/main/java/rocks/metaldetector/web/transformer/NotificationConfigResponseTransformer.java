package rocks.metaldetector.web.transformer;

import org.springframework.stereotype.Component;
import rocks.metaldetector.service.notification.config.NotificationConfigDto;
import rocks.metaldetector.service.notification.config.TelegramConfigDto;
import rocks.metaldetector.web.api.EmailConfig;
import rocks.metaldetector.web.api.TelegramConfig;
import rocks.metaldetector.web.api.request.UpdateNotificationConfigRequest;
import rocks.metaldetector.web.api.response.NotificationConfigResponse;

import java.util.List;

import static rocks.metaldetector.persistence.domain.notification.NotificationChannel.EMAIL;
import static rocks.metaldetector.persistence.domain.notification.NotificationChannel.TELEGRAM;

@Component
public class NotificationConfigResponseTransformer {

  public NotificationConfigResponse transformResponse(List<NotificationConfigDto> notificationConfigs, TelegramConfigDto telegramConfig) {
    return NotificationConfigResponse.builder()
        .emailConfig(transformEmailConfig(notificationConfigs))
        .telegramConfig(transformTelegramConfig(notificationConfigs, telegramConfig))
        .build();
  }

  public NotificationConfigDto transformUpdateRequest(UpdateNotificationConfigRequest request) {
    return NotificationConfigDto.builder()
        .channel(request.getChannel())
        .frequencyInWeeks(request.getFrequencyInWeeks())
        .notificationAtAnnouncementDate(request.isNotificationAtAnnouncementDate())
        .notificationAtReleaseDate(request.isNotificationAtReleaseDate())
        .notify(request.isNotify())
        .build();
  }

  private EmailConfig transformEmailConfig(List<NotificationConfigDto> notificationConfigs) {
    EmailConfig emailConfig = new EmailConfig();
    NotificationConfigDto notificationConfig = notificationConfigs.stream()
        .filter(config -> config.getChannel().equals(EMAIL.name()))
        .findAny().orElse(null);

    if (notificationConfig != null) {
      emailConfig.setNotify(notificationConfig.isNotify());
      emailConfig.setFrequencyInWeeks(notificationConfig.getFrequencyInWeeks());
      emailConfig.setNotificationAtReleaseDate(notificationConfig.isNotificationAtReleaseDate());
      emailConfig.setNotificationAtAnnouncementDate(notificationConfig.isNotificationAtAnnouncementDate());
    }
    return emailConfig;
  }

  private TelegramConfig transformTelegramConfig(List<NotificationConfigDto> notificationConfigs, TelegramConfigDto telegramConfigDto) {
    TelegramConfig telegramConfig = new TelegramConfig();
    NotificationConfigDto notificationConfig = notificationConfigs.stream()
        .filter(config -> config.getChannel().equals(TELEGRAM.name()))
        .findAny().orElse(null);

    if (notificationConfig != null) {
      telegramConfig.setNotify(notificationConfig.isNotify());
      telegramConfig.setFrequencyInWeeks(notificationConfig.getFrequencyInWeeks());
      telegramConfig.setNotificationAtReleaseDate(notificationConfig.isNotificationAtReleaseDate());
      telegramConfig.setNotificationAtAnnouncementDate(notificationConfig.isNotificationAtAnnouncementDate());
      telegramConfig.setRegistrationId(telegramConfigDto.getRegistrationId());
      telegramConfig.setNotificationsActivated(telegramConfigDto.getChatId() != null);
    }
    return telegramConfig;
  }
}
