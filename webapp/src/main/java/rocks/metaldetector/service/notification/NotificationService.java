package rocks.metaldetector.service.notification;

public interface NotificationService {

  void notifyOnFrequency();
  void notifyOnReleaseDate();
  void notifyOnAnnouncementDate();

  NotificationConfigDto getCurrentUserNotificationConfig();
  void updateCurrentUserNotificationConfig(NotificationConfigDto notificationConfigDto);
  void updateTelegramChatId(long userId, int chatId);
}
