package rocks.metaldetector.service.notification;

import java.util.Optional;

public interface TelegramConfigService {

  Optional<TelegramConfigDto> getCurrentUserTelegramConfig();

  int generateRegistrationId();
  void updateChatId(String messageText, int chatId);
}
