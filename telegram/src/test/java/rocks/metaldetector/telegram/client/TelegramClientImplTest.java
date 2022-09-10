package rocks.metaldetector.telegram.client;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestOperations;
import rocks.metaldetector.support.exceptions.ExternalServiceException;
import rocks.metaldetector.telegram.api.TelegramMessage;
import rocks.metaldetector.telegram.api.TelegramSendMessageRequest;
import rocks.metaldetector.telegram.config.TelegramProperties;

import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static rocks.metaldetector.telegram.client.TelegramClientImpl.METHOD_ENDPOINT_NAME;

@ExtendWith(MockitoExtension.class)
class TelegramClientImplTest implements WithAssertions {

  @Mock
  private RestOperations restOperations;

  @Mock
  private TelegramProperties properties;

  @InjectMocks
  private TelegramClientImpl underTest;

  @AfterEach
  void tearDown() {
    reset(restOperations, properties);
  }

  @Test
  @DisplayName("correct url is called on restOperations")
  void test_correct_url_called() {
    // given
    var url = "url";
    doReturn(url).when(properties).getRestBaseUrl();
    doReturn("botId").when(properties).getBotId();
    doReturn(ResponseEntity.ok().body(new TelegramMessage())).when(restOperations)
        .postForEntity(anyString(), any(), any(), anyString());

    // when
    underTest.sendMessage(new TelegramSendMessageRequest());

    // then
    verify(restOperations).postForEntity(eq(url + METHOD_ENDPOINT_NAME), any(), eq(TelegramMessage.class), anyString());
  }

  @Test
  @DisplayName("restOperations is called with correct request")
  void test_correct_request() {
    // given
    doReturn("botId").when(properties).getBotId();
    doReturn(ResponseEntity.ok().body(new TelegramMessage())).when(restOperations)
        .postForEntity(anyString(), any(), any(), anyString());
    var request = new TelegramSendMessageRequest();

    // when
    underTest.sendMessage(request);

    // then
    verify(restOperations).postForEntity(anyString(), eq(request), eq(TelegramMessage.class), anyString());
  }

  @Test
  @DisplayName("restOperations is called with correct url parameter")
  void test_correct_url_parameter() {
    // given
    var botId = "botId";
    doReturn(botId).when(properties).getBotId();
    doReturn(ResponseEntity.ok().body(new TelegramMessage())).when(restOperations)
        .postForEntity(anyString(), any(), any(), anyString());

    // when
    underTest.sendMessage(new TelegramSendMessageRequest());

    // then
    verify(restOperations).postForEntity(anyString(), any(), eq(TelegramMessage.class), eq(botId));
  }

  @Test
  @DisplayName("sent message is returned")
  void test_sent_message_returned() {
    // given
    var responseMessage = new TelegramMessage();
    doReturn("botId").when(properties).getBotId();
    doReturn(ResponseEntity.ok().body(responseMessage)).when(restOperations)
        .postForEntity(anyString(), any(), any(), anyString());

    // when
    var result = underTest.sendMessage(new TelegramSendMessageRequest());

    // then
    assertThat(result).isEqualTo(responseMessage);
  }

  @Test
  @DisplayName("if the response message is null, an ExternalServiceException is thrown")
  void test_exception_if_releases_response_null() {
    // given
    var botId = "botId";
    doReturn(botId).when(properties).getBotId();
    doReturn(ResponseEntity.ok().build()).when(restOperations)
        .postForEntity(anyString(), any(), any(), anyString());

    // when
    Throwable throwable = catchThrowable(() -> underTest.sendMessage(new TelegramSendMessageRequest()));

    // then
    assertThat(throwable).isInstanceOf(ExternalServiceException.class);
  }

  @ParameterizedTest(name = "If the status is {0}, an ExternalServiceException is thrown")
  @MethodSource("httpStatusCodeProvider")
  @DisplayName("If the status code is not OK on query, an ExternalServiceException is thrown")
  void test_releases_exception_if_status_is_not_ok(HttpStatus httpStatus) {
    // given
    var botId = "botId";
    doReturn(botId).when(properties).getBotId();
    doReturn(ResponseEntity.status(httpStatus).body(new TelegramMessage())).when(restOperations)
        .postForEntity(anyString(), any(), any(), anyString());

    // when
    Throwable throwable = catchThrowable(() -> underTest.sendMessage(new TelegramSendMessageRequest()));

    // then
    assertThat(throwable).isInstanceOf(ExternalServiceException.class);
  }

  private static Stream<Arguments> httpStatusCodeProvider() {
    return Stream.of(HttpStatus.values()).filter(status -> !status.is2xxSuccessful()).map(Arguments::of);
  }
}
