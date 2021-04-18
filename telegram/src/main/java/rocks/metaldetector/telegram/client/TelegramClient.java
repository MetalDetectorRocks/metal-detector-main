package rocks.metaldetector.telegram.client;

import rocks.metaldetector.telegram.api.TelegramMessage;
import rocks.metaldetector.telegram.api.TelegramSendMessageRequest;

public interface TelegramClient {

  TelegramMessage sendMessage(TelegramSendMessageRequest request);
}
