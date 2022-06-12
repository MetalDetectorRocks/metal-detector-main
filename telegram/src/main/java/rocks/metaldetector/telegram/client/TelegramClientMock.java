package rocks.metaldetector.telegram.client;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import rocks.metaldetector.telegram.api.TelegramChat;
import rocks.metaldetector.telegram.api.TelegramMessage;
import rocks.metaldetector.telegram.api.TelegramSendMessageRequest;

@Component
@Profile("mockmode")
public class TelegramClientMock implements TelegramClient {

  @Override
  public TelegramMessage sendMessage(TelegramSendMessageRequest request) {
    return new TelegramMessage(request.getText(), new TelegramChat(request.getChatId()), "Some mock description");
  }
}
