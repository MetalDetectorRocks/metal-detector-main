package rocks.metaldetector.telegram.facade;

public interface TelegramMessagingService {

  void sendMessage(int telegramChatId, String message);
}
