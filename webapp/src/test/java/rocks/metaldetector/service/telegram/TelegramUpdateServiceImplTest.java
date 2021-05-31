package rocks.metaldetector.service.telegram;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rocks.metaldetector.service.notification.config.TelegramConfigService;
import rocks.metaldetector.web.api.request.TelegramChat;
import rocks.metaldetector.web.api.request.TelegramMessage;
import rocks.metaldetector.web.api.request.TelegramUpdate;

import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class TelegramUpdateServiceImplTest implements WithAssertions {

  @Mock
  private TelegramConfigService telegramConfigService;

  @InjectMocks
  private TelegramUpdateServiceImpl underTest;

  @AfterEach
  private void tearDown() {
    reset(telegramConfigService);
  }

  @Test
  @DisplayName("notificationService is called with correct ids")
  void test_user_repository_called() {
    // given
    var messageText = "123456";
    var chatId = 666;
    var update = new TelegramUpdate(new TelegramMessage(messageText, new TelegramChat(chatId)));

    // when
    underTest.processUpdate(update);

    // then
    verify(telegramConfigService).updateChatId(messageText, chatId);
  }

  @Test
  @DisplayName("nothing is called if the conversation with the bot has just started")
  void test_nothing_called() {
    // given
    var messageText = "/start";
    var chatId = 666;
    var update = new TelegramUpdate(new TelegramMessage(messageText, new TelegramChat(chatId)));

    // when
    underTest.processUpdate(update);

    // then
    verifyNoInteractions(telegramConfigService);
  }
}
