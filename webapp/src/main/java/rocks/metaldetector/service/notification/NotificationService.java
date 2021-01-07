package rocks.metaldetector.service.notification;

public interface NotificationService {

  void notifyAllUsers();

  NotificationConfigDto getCurrentUserNotificationConfig();
  void updateCurrentUserNotificationConfig(NotificationConfigDto notificationConfigDto);
}
