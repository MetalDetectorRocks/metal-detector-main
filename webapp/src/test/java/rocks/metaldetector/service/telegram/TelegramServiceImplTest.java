package rocks.metaldetector.service.telegram;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rocks.metaldetector.persistence.domain.notification.TelegramConfigEntity;
import rocks.metaldetector.persistence.domain.notification.TelegramConfigRepository;
import rocks.metaldetector.service.notification.config.TelegramConfigService;
import rocks.metaldetector.service.user.UserEntityFactory;
import rocks.metaldetector.telegram.facade.TelegramMessagingService;
import rocks.metaldetector.web.api.request.TelegramChat;
import rocks.metaldetector.web.api.request.TelegramMessage;
import rocks.metaldetector.web.api.request.TelegramUpdate;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static rocks.metaldetector.service.telegram.TelegramServiceImpl.FIRST_BOT_MESSAGE_TEXT;
import static rocks.metaldetector.service.telegram.TelegramServiceImpl.FIRST_BOT_RESPONSE_TEXT;

@ExtendWith(MockitoExtension.class)
class TelegramServiceImplTest implements WithAssertions {

  @Mock
  private TelegramConfigService telegramConfigService;

  @Mock
  private TelegramMessagingService telegramMessagingService;

  @Mock
  private TelegramConfigRepository telegramConfigRepository;

  @InjectMocks
  private TelegramServiceImpl underTest;

  @AfterEach
  void tearDown() {
    reset(telegramConfigService, telegramMessagingService, telegramConfigRepository);
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

  @Test
  @DisplayName("sendMessage: telegramConfigRepository is called")
  void test_telegram_config_repository_called() {
    // given
    var user = UserEntityFactory.createDefaultUser();

    //when
    underTest.sendMessage(user, "message");

    //then
    verify(telegramConfigRepository).findByUser(user);
  }

  @Test
  @DisplayName("sendMessage: telegramMessagingService is called with chatId")
  void test_telegram_messaging_service_called_with_chat_id() {
    // given
    var user = UserEntityFactory.createDefaultUser();
    var telegramConfig = TelegramConfigEntity.builder().chatId(666).build();
    doReturn(Optional.of(telegramConfig)).when(telegramConfigRepository).findByUser(any());

    //when
    underTest.sendMessage(user, "message");

    //then
    verify(telegramMessagingService).sendMessage(eq(telegramConfig.getChatId()), any());
  }

  @Test
  @DisplayName("sendMessage: telegramMessagingService is called with message")
  void test_telegram_messaging_service_called_with_message() {
    // given
    var user = UserEntityFactory.createDefaultUser();
    var telegramConfig = TelegramConfigEntity.builder().chatId(666).build();
    var expectedMessage = "message";
    doReturn(Optional.of(telegramConfig)).when(telegramConfigRepository).findByUser(any());

    //when
    underTest.sendMessage(user, "message");

    //then
    verify(telegramMessagingService).sendMessage(anyInt(), eq(expectedMessage));
  }

  @Test
  @DisplayName("sendMessage: telegramMessagingService is not called if telegram config does not exist")
  void test_telegram_messaging_service_not_called_without_config() {
    // given
    var user = UserEntityFactory.createDefaultUser();
    doReturn(Optional.empty()).when(telegramConfigRepository).findByUser(any());

    //when
    underTest.sendMessage(user, "message");

    //then
    verifyNoInteractions(telegramMessagingService);
  }

  @Test
  @DisplayName("sendMessage: telegramMessagingService is not called if chatId is null")
  void test_telegram_messaging_service_not_called_without_chat_id() {
    // given
    var user = UserEntityFactory.createDefaultUser();
    var telegramConfig = TelegramConfigEntity.builder().build();
    doReturn(Optional.of(telegramConfig)).when(telegramConfigRepository).findByUser(any());

    //when
    underTest.sendMessage(user, "message");

    //then
    verifyNoInteractions(telegramMessagingService);
  }
}
