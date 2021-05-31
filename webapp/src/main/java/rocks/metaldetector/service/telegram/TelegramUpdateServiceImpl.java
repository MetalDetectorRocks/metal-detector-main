package rocks.metaldetector.service.telegram;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import rocks.metaldetector.service.notification.config.TelegramConfigService;
import rocks.metaldetector.web.api.request.TelegramUpdate;

@Component
@AllArgsConstructor
@Slf4j
public class TelegramUpdateServiceImpl implements TelegramUpdateService {

  static final String FIRST_MESSAGE_TEXT = "/start";

  private final TelegramConfigService telegramConfigService;

  @Override
  public void processUpdate(TelegramUpdate update) {
    if (!isFirstBotMessage(update)) {
      registerForTelegramNotifications(update);
    }
  }

  private void registerForTelegramNotifications(TelegramUpdate update) {
    String messageText = update.getMessage().getText();
    int chatId = update.getMessage().getChat().getId();
    telegramConfigService.updateChatId(messageText, chatId);
  }

  private boolean isFirstBotMessage(TelegramUpdate update) {
    return update.getMessage().getText().equals(FIRST_MESSAGE_TEXT);
  }
}
