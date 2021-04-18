package rocks.metaldetector.telegram.facade;

public interface TelegramService {

  void sendMessage(int telegramChatId, String message);
}
