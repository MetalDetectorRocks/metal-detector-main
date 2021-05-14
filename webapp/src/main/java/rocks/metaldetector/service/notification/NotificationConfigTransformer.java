package rocks.metaldetector.service.notification;

import org.springframework.stereotype.Component;
import rocks.metaldetector.persistence.domain.notification.NotificationConfigEntity;

@Component
public class NotificationConfigTransformer {

  public NotificationConfigDto transform(NotificationConfigEntity notificationConfig) {
    return NotificationConfigDto.builder()
        .notify(notificationConfig.getNotify())
        .notificationAtReleaseDate(notificationConfig.getNotificationAtReleaseDate())
        .notificationAtAnnouncementDate(notificationConfig.getNotificationAtAnnouncementDate())
        .frequencyInWeeks(notificationConfig.getFrequencyInWeeks())
        .channel(notificationConfig.getChannel().name())
        .build();
  }
}
