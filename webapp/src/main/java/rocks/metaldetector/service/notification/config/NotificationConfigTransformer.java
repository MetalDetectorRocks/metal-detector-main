package rocks.metaldetector.service.notification.config;

import org.springframework.stereotype.Component;
import rocks.metaldetector.persistence.domain.notification.NotificationConfigEntity;

@Component
public class NotificationConfigTransformer {

  public NotificationConfigDto transform(NotificationConfigEntity notificationConfig) {
    return NotificationConfigDto.builder()
        .notificationAtReleaseDate(notificationConfig.getNotificationAtReleaseDate())
        .notificationAtAnnouncementDate(notificationConfig.getNotificationAtAnnouncementDate())
        .frequencyInWeeks(notificationConfig.getFrequencyInWeeks())
        .notifyReissues(notificationConfig.getNotifyReissues())
        .channel(notificationConfig.getChannel().name())
        .build();
  }
}
