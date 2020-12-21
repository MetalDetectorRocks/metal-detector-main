package rocks.metaldetector.service.notification;

import org.springframework.stereotype.Component;
import rocks.metaldetector.persistence.domain.notification.NotificationConfigEntity;

@Component
public class NotificationConfigDtoTransformer {

  public NotificationConfigEntity transform(NotificationConfigDto notificationConfigDto) {
    return NotificationConfigEntity.builder()
        .notify(notificationConfigDto.isNotify())
        .notificationAtReleaseDate(notificationConfigDto.isNotificationAtReleaseDate())
        .notificationAtAnnouncementDate(notificationConfigDto.isNotificationAtAnnouncementDate())
        .frequencyInWeeks(notificationConfigDto.getFrequencyInWeeks())
        .build();
  }
}
