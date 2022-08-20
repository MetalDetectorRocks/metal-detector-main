package rocks.metaldetector.telegram.facade;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rocks.metaldetector.telegram.api.TelegramSendMessageRequest;
import rocks.metaldetector.telegram.client.TelegramClient;

import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TelegramMessagingServiceImplTest implements WithAssertions {

  @Mock
  private TelegramClient telegramClient;

  @InjectMocks
  private TelegramMessagingServiceImpl underTest;

  @AfterEach
  void tearDown() {
    reset(telegramClient);
  }

  @Test
  @DisplayName("telegramClient is called with expected request")
  void test_client_called() {
    // given
    var chatId = 12345;
    var message = "message";
    var request = new TelegramSendMessageRequest(chatId, message);

    // when
    underTest.sendMessage(chatId, message);

    // then
    verify(telegramClient).sendMessage(request);
  }
}
