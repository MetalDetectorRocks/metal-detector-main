package rocks.metaldetector.telegram.client;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import rocks.metaldetector.telegram.api.TelegramSendMessageRequest;

class TelegramClientMockTest implements WithAssertions {

  TelegramClientMock underTest = new TelegramClientMock();

  @Test
  @DisplayName("Mock with given attributes is returned")
  void test_mock_not_null() {
    // given
    var request = new TelegramSendMessageRequest(666, "someText");

    // when
    var result = underTest.sendMessage(request);

    //then
    assertThat(result).isNotNull();
    assertThat(result.getText()).isEqualTo(request.getText());
    assertThat(result.getChat()).isNotNull();
    assertThat(result.getChat().getId()).isEqualTo(request.getChatId());
    assertThat(result.getDescription()).isNotNull();
  }
}
