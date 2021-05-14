package rocks.metaldetector.service.telegram;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import rocks.metaldetector.service.notification.TelegramConfigService;
import rocks.metaldetector.web.api.request.TelegramUpdate;

@Component
@AllArgsConstructor
@Slf4j
public class TelegramUpdateServiceImpl implements TelegramUpdateService {

  private final TelegramConfigService telegramConfigService;

  @Override
  public void processUpdate(TelegramUpdate update) {
    registerForTelegramNotifications(update);
  }

  private void registerForTelegramNotifications(TelegramUpdate update) {
    String messageText = update.getMessage().getText();
    int chatId = update.getMessage().getChat().getId();
    telegramConfigService.updateChatId(messageText, chatId);
  }
}
