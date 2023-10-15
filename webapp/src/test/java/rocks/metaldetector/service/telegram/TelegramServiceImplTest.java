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
import rocks.metaldetector.telegram.facade.TelegramMessagingService;
import rocks.metaldetector.web.api.request.TelegramChat;
import rocks.metaldetector.web.api.request.TelegramMessage;
import rocks.metaldetector.web.api.request.TelegramUpdate;

import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static rocks.metaldetector.service.telegram.TelegramServiceImpl.FIRST_BOT_MESSAGE_TEXT;
import static rocks.metaldetector.service.telegram.TelegramServiceImpl.FIRST_BOT_RESPONSE_TEXT;

@ExtendWith(MockitoExtension.class)
class TelegramServiceImplTest implements WithAssertions {

  @Mock
  private TelegramConfigService telegramConfigService;

  @Mock
  private TelegramMessagingService telegramMessagingService;

  @InjectMocks
  private TelegramServiceImpl underTest;

  @AfterEach
  void tearDown() {
    reset(telegramConfigService, telegramMessagingService);
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
  @DisplayName("telegramMessagingService is called if the conversation with the bot has just started")
  void test_nothing_called() {
    // given
    var chatId = 666;
    var update = new TelegramUpdate(new TelegramMessage(FIRST_BOT_MESSAGE_TEXT, new TelegramChat(chatId)));

    // when
    underTest.processUpdate(update);

    // then
    verify(telegramMessagingService).sendMessage(chatId, FIRST_BOT_RESPONSE_TEXT);
  }
}
