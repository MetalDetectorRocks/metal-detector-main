package rocks.metaldetector.telegram.facade;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import rocks.metaldetector.telegram.api.TelegramSendMessageRequest;
import rocks.metaldetector.telegram.client.TelegramClient;

@Component
@AllArgsConstructor
public class TelegramMessagingServiceImpl implements TelegramMessagingService {

  private final TelegramClient telegramClient;

  @Override
  public void sendMessage(int chatId, String message) {
    TelegramSendMessageRequest request = new TelegramSendMessageRequest(chatId, message);
    telegramClient.sendMessage(request);
  }
}
