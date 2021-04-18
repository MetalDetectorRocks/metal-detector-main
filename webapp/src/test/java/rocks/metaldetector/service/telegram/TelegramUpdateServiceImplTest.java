package rocks.metaldetector.service.telegram;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rocks.metaldetector.persistence.domain.user.AbstractUserEntity;
import rocks.metaldetector.persistence.domain.user.UserRepository;
import rocks.metaldetector.service.notification.NotificationService;
import rocks.metaldetector.web.api.request.TelegramChat;
import rocks.metaldetector.web.api.request.TelegramMessage;
import rocks.metaldetector.web.api.request.TelegramUpdate;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TelegramUpdateServiceImplTest implements WithAssertions {

  @Mock
  private NotificationService notificationService;

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private TelegramUpdateServiceImpl underTest;

  @AfterEach
  private void tearDown() {
    reset(notificationService, userRepository);
  }

  @Test
  @DisplayName("userRepository is called with email address from message")
  void test_user_repository_called() {
    // given
    var messageText = "text";
    var update = new TelegramUpdate(new TelegramMessage(messageText, null));

    // when
    underTest.processUpdate(update);

    // then
    verify(userRepository).findByEmail(messageText);
  }

  @Test
  @DisplayName("if user is present, notificationService is called to set telegram chat id")
  void test_notification_service_called() {
    // given
    var user = mock(AbstractUserEntity.class);
    var chatId = 666;
    var update = new TelegramUpdate(new TelegramMessage(null, new TelegramChat(chatId)));
    doReturn(555L).when(user).getId();
    doReturn(Optional.of(user)).when(userRepository).findByEmail(any());

    // when
    underTest.processUpdate(update);

    // then
    verify(notificationService).updateTelegramChatId(user.getId(), chatId);
  }
}