package rocks.metaldetector.service.notification;

import org.springframework.stereotype.Component;
import rocks.metaldetector.persistence.domain.notification.NotificationConfigEntity;

@Component
public class NotificationConfigTransformer {

  public NotificationConfigDto transform(NotificationConfigEntity notificationConfigentity) {
    return NotificationConfigDto.builder()
        .notify(notificationConfigentity.getNotify())
        .notificationAtReleaseDate(notificationConfigentity.getNotificationAtReleaseDate())
        .notificationAtAnnouncementDate(notificationConfigentity.getNotificationAtAnnouncementDate())
        .frequencyInWeeks(notificationConfigentity.getFrequencyInWeeks())
        .telegramChatId(notificationConfigentity.getTelegramChatId())
        .build();
  }
}
