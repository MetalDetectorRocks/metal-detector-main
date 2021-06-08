package rocks.metaldetector.web.controller.rest;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rocks.metaldetector.service.notification.config.TelegramConfigService;
import rocks.metaldetector.support.Endpoints;
import rocks.metaldetector.web.RestAssuredMockMvcUtils;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpStatus.OK;

@ExtendWith(MockitoExtension.class)
class TelegramConfigRestControllerTest implements WithAssertions {

  @Mock
  private TelegramConfigService telegramConfigService;

  @InjectMocks
  private TelegramConfigRestController underTest;

  private RestAssuredMockMvcUtils restAssuredMockMvcUtils;

  @BeforeEach
  void setup() {
    restAssuredMockMvcUtils = new RestAssuredMockMvcUtils(Endpoints.Rest.TELEGRAM_CONFIG);
    RestAssuredMockMvc.standaloneSetup(underTest);
  }

  @AfterEach
  void tearDown() {
    reset(telegramConfigService);
  }

  @Test
  @DisplayName("Generating a registration id should return 200")
  void test_post_returns_200() {
    // when
    var validatableResponse = restAssuredMockMvcUtils.doPost();

    // then
    validatableResponse.statusCode(OK.value());
  }

  @Test
  @DisplayName("Generating a registration id calls service")
  void test_post_calls_service() {
    // when
    restAssuredMockMvcUtils.doPost();

    // then
    verify(telegramConfigService).generateRegistrationId();
  }

  @Test
  @DisplayName("Generating a registration id returns id")
  void test_post_returns_id() {
    // given
    var expectedId = 666_666;
    doReturn(expectedId).when(telegramConfigService).generateRegistrationId();

    // when
    var validatableResponse = restAssuredMockMvcUtils.doPost();

    // then
    var response = validatableResponse.extract().as(Integer.class);
    assertThat(response).isEqualTo(expectedId);
  }

  @Test
  @DisplayName("Deleting the telegram config should return 200")
  void test_delete_returns_200() {
    // when
    var validatableResponse = restAssuredMockMvcUtils.doDelete();

    // then
    validatableResponse.statusCode(OK.value());
  }

  @Test
  @DisplayName("Deleting the telegram config should call service")
  void test_delete_calls_service() {
    // when
    restAssuredMockMvcUtils.doDelete();

    // then
    verify(telegramConfigService).deleteCurrentUserTelegramConfig();
  }
}
