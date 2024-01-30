package rocks.metaldetector.web.controller.rest;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rocks.metaldetector.service.telegram.TelegramService;
import rocks.metaldetector.service.telegram.TelegramUpdateFactory;
import rocks.metaldetector.support.Endpoints;
import rocks.metaldetector.web.RestAssuredMockMvcUtils;
import rocks.metaldetector.web.api.request.TelegramChat;
import rocks.metaldetector.web.api.request.TelegramMessage;
import rocks.metaldetector.web.api.request.TelegramUpdate;

import java.util.stream.Stream;

import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.OK;

@ExtendWith(MockitoExtension.class)
class TelegramRestControllerTest implements WithAssertions {

  @Mock
  private TelegramService telegramService;

  private RestAssuredMockMvcUtils restAssuredUtils;

  @BeforeEach
  void setup() {
    TelegramRestController underTest = new TelegramRestController(telegramService, "botId");
    restAssuredUtils = new RestAssuredMockMvcUtils(Endpoints.Rest.NOTIFICATION_TELEGRAM);
    RestAssuredMockMvc.standaloneSetup(underTest);
  }

  @AfterEach
  void tearDown() {
    reset(telegramService);
  }

  @Test
  @DisplayName("telegramUpdateService is called if botId is correct")
  void test_telegram_update_service_called() {
    // given
    var update = TelegramUpdateFactory.createDefault();

    // when
    restAssuredUtils.doPost(update, "/botId");

    // then
    verify(telegramService).processUpdate(update);
  }

  @Test
  @DisplayName("Http status 200 is returned if botId is correct")
  void test_status_ok() {
    // given
    var update = TelegramUpdateFactory.createDefault();

    // when
    var result = restAssuredUtils.doPost(update, "/botId");

    // then
    assertThat(result.status(OK));
  }

  @Test
  @DisplayName("telegramUpdateService is not called if botId is incorrect")
  void test_telegram_update_service_not_called() {
    // given
    var update = TelegramUpdateFactory.createDefault();

    // when
    restAssuredUtils.doPost(update, "/wrongBotId");

    // then
    verifyNoInteractions(telegramService);
  }

  @ParameterizedTest
  @DisplayName("bad request request is returned for wrong inputs")
  @MethodSource("inputProvider")
  void test_telegram_update_service_not_called_bad_request(TelegramUpdate update) {
    // when
    var result = restAssuredUtils.doPost(update, "/botId");

    // then
    assertThat(result.status(BAD_REQUEST));
  }

  @Test
  @DisplayName("Http status 403 is returned if botId is incorrect")
  void test_status_forbidden() {
    // given
    var update = TelegramUpdateFactory.createDefault();

    // when
    var result = restAssuredUtils.doPost(update, "/wrongBotId");

    // then
    assertThat(result.status(FORBIDDEN));
  }

  private static Stream<Arguments> inputProvider() {
    var messageWithoutChat = new TelegramMessage("text", null);
    var messageWithoutText = new TelegramMessage(null, new TelegramChat(0));
    var messageWithoutText2 = new TelegramMessage("", new TelegramChat(0));
    var messageWithChatWithoutId = new TelegramMessage("text", new TelegramChat(null));
    return Stream.of(
        Arguments.of(new TelegramUpdate()),
        Arguments.of(new TelegramUpdate(messageWithoutChat)),
        Arguments.of(new TelegramUpdate(messageWithoutText)),
        Arguments.of(new TelegramUpdate(messageWithoutText2)),
        Arguments.of(new TelegramUpdate(messageWithChatWithoutId))
    );
  }
}
