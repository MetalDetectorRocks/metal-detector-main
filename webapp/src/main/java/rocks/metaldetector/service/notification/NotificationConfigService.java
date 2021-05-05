package rocks.metaldetector.service.notification;

public interface NotificationConfigService {

  NotificationConfigDto getCurrentUserNotificationConfig();
  void updateCurrentUserNotificationConfig(NotificationConfigDto notificationConfigDto);

  void updateTelegramChatId(int registrationId, int chatId);
  int generateTelegramRegistrationId();
  void deactivateTelegramNotifications();
}
