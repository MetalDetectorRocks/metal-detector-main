package rocks.metaldetector.service.notification;

public interface NotificationService {

  void notifyOnFrequency();

  NotificationConfigDto getCurrentUserNotificationConfig();
  void updateCurrentUserNotificationConfig(NotificationConfigDto notificationConfigDto);
}
