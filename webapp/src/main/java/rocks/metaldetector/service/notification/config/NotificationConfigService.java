package rocks.metaldetector.service.notification.config;

import java.util.List;

public interface NotificationConfigService {

  List<NotificationConfigDto> getCurrentUserNotificationConfigs();
  void updateCurrentUserNotificationConfig(NotificationConfigDto notificationConfigDto);
}
