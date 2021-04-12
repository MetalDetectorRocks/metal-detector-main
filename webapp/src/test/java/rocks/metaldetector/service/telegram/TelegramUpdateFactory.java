package rocks.metaldetector.service.telegram;

import rocks.metaldetector.web.api.request.TelegramChat;
import rocks.metaldetector.web.api.request.TelegramMessage;
import rocks.metaldetector.web.api.request.TelegramUpdate;

public class TelegramUpdateFactory {

  public static TelegramUpdate createDefault() {
    return TelegramUpdate.builder().message(
        TelegramMessage.builder()
            .text("text")
            .chat(
                TelegramChat.builder().id(666).build()
            ).build()
    ).build();
  }
}
