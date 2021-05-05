package rocks.metaldetector.service.telegram;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rocks.metaldetector.persistence.domain.user.UserRepository;
import rocks.metaldetector.service.notification.NotificationConfigService;
import rocks.metaldetector.web.api.request.TelegramChat;
import rocks.metaldetector.web.api.request.TelegramMessage;
import rocks.metaldetector.web.api.request.TelegramUpdate;

import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TelegramUpdateServiceImplTest implements WithAssertions {

  @Mock
  private NotificationConfigService notificationConfigService;

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private TelegramUpdateServiceImpl underTest;

  @AfterEach
  private void tearDown() {
    reset(notificationConfigService, userRepository);
  }

  @Test
  @DisplayName("notificationService is called with correct ids")
  void test_user_repository_called() {
    // given
    var messageText = "123456";
    var update = new TelegramUpdate(new TelegramMessage(messageText, new TelegramChat(666)));

    // when
    underTest.processUpdate(update);

    // then
    verify(notificationConfigService).updateTelegramChatId(Integer.parseInt(messageText), update.getMessage().getChat().getId());
  }
}
