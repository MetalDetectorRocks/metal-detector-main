package rocks.metaldetector.service.notification;

public interface NotificationService {

  void notifyOnFrequency();
  void notifyOnReleaseDate();

  NotificationConfigDto getCurrentUserNotificationConfig();
  void updateCurrentUserNotificationConfig(NotificationConfigDto notificationConfigDto);
}
