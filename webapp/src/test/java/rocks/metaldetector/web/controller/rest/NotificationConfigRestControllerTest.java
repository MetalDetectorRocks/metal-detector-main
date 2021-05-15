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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rocks.metaldetector.service.notification.config.NotificationConfigDto;
import rocks.metaldetector.service.notification.config.NotificationConfigService;
import rocks.metaldetector.service.notification.config.TelegramConfigDto;
import rocks.metaldetector.service.notification.config.TelegramConfigService;
import rocks.metaldetector.web.RestAssuredMockMvcUtils;
import rocks.metaldetector.web.api.request.UpdateNotificationConfigRequest;
import rocks.metaldetector.web.api.response.EmailConfig;
import rocks.metaldetector.web.api.response.NotificationConfigResponse;
import rocks.metaldetector.web.api.response.TelegramConfig;
import rocks.metaldetector.web.transformer.NotificationConfigResponseTransformer;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;
import static rocks.metaldetector.support.Endpoints.Rest.NOTIFICATION_CONFIG;

@ExtendWith(MockitoExtension.class)
class NotificationConfigRestControllerTest implements WithAssertions {

  @Mock
  private NotificationConfigService notificationConfigService;

  @Mock
  private TelegramConfigService telegramConfigService;

  @Mock
  private NotificationConfigResponseTransformer responseTransformer;

  @InjectMocks
  private NotificationConfigRestController underTest;

  private RestAssuredMockMvcUtils restAssuredMockMvcUtils;

  @BeforeEach
  void setup() {
    restAssuredMockMvcUtils = new RestAssuredMockMvcUtils(NOTIFICATION_CONFIG);
    RestAssuredMockMvc.standaloneSetup(underTest);
  }

  @AfterEach
  void tearDown() {
    reset(notificationConfigService, telegramConfigService, responseTransformer);
  }

  @Test
  @DisplayName("Getting the current user's notification configs should return 200")
  void test_get_returns_200() {
    // when
    var validatableResponse = restAssuredMockMvcUtils.doGet();

    // then
    validatableResponse.statusCode(OK.value());
  }

  @Test
  @DisplayName("Getting the current user's notification configs calls notificationConfigService")
  void test_get_calls_notification_config_service() {
    // when
    restAssuredMockMvcUtils.doGet();

    // then
    verify(notificationConfigService).getCurrentUserNotificationConfigs();
  }

  @Test
  @DisplayName("Getting the current user's notification configs calls telegramConfigService")
  void test_get_calls_telegram_config_service() {
    // when
    restAssuredMockMvcUtils.doGet();

    // then
    verify(telegramConfigService).getCurrentUserTelegramConfig();
  }

  @Test
  @DisplayName("Getting the current user's notification configs calls transformer")
  void test_get_calls_transformer() {
    // given
    var notificationConfigs = List.of(NotificationConfigDto.builder().build(),NotificationConfigDto.builder().build());
    var telegramConfig = TelegramConfigDto.builder().build();
    doReturn(notificationConfigs).when(notificationConfigService).getCurrentUserNotificationConfigs();
    doReturn(Optional.of(telegramConfig)).when(telegramConfigService).getCurrentUserTelegramConfig();

    // when
    restAssuredMockMvcUtils.doGet();

    // then
    verify(responseTransformer).transformResponse(notificationConfigs, telegramConfig);
  }

  @Test
  @DisplayName("transformer is called null null if not telegramConfig is present")
  void test_get_calls_transformer_with_null() {
    // given
    doReturn(Collections.emptyList()).when(notificationConfigService).getCurrentUserNotificationConfigs();
    doReturn(Optional.empty()).when(telegramConfigService).getCurrentUserTelegramConfig();

    // when
    restAssuredMockMvcUtils.doGet();

    // then
    verify(responseTransformer).transformResponse(anyList(), eq(null));
  }

  @Test
  @DisplayName("Getting the current user's notification configs returns response")
  void test_get_returns_response() {
    // given
    var expectedResponse = NotificationConfigResponse.builder()
        .emailConfig(new EmailConfig())
        .telegramConfig(new TelegramConfig())
        .build();
    doReturn(expectedResponse).when(responseTransformer).transformResponse(any(), any());

    // when
    var validatableResponse = restAssuredMockMvcUtils.doGet();

    // then
    var response = validatableResponse.extract().as(NotificationConfigResponse.class);
    assertThat(response).isEqualTo(expectedResponse);
  }

  @Test
  @DisplayName("Updating the current user's notification config should return 200")
  void test_put_returns_200() {
    // given
    var updateNotificationConfigRequest = UpdateNotificationConfigRequest.builder().frequencyInWeeks(4).channel("CHANNEL").build();

    // when
    var validatableResponse = restAssuredMockMvcUtils.doPut(updateNotificationConfigRequest);

    // then
    validatableResponse.statusCode(OK.value());
  }

  @ParameterizedTest
  @MethodSource("badRequestInputProvider")
  @DisplayName("Updating the current user's notification config should return 400")
  void test_put_returns_400(UpdateNotificationConfigRequest request) {
    // when
    var validatableResponse = restAssuredMockMvcUtils.doPut(request);

    // then
    validatableResponse.statusCode(BAD_REQUEST.value());
  }

  @Test
  @DisplayName("Updating the current user's notification config calls transformer")
  void test_put_calls_transformer() {
    // given
    var updateNotificationConfigRequest = UpdateNotificationConfigRequest.builder().frequencyInWeeks(4).channel("channel").build();

    // when
    restAssuredMockMvcUtils.doPut(updateNotificationConfigRequest);

    // then
    verify(responseTransformer).transformUpdateRequest(updateNotificationConfigRequest);
  }

  @Test
  @DisplayName("Updating the current user's notification config calls service")
  void test_put_calls_service() {
    // given
    var updateNotificationConfigRequest = UpdateNotificationConfigRequest.builder().frequencyInWeeks(4).channel("channel").build();
    var notificationConfigDto = NotificationConfigDto.builder().frequencyInWeeks(4).build();
    doReturn(notificationConfigDto).when(responseTransformer).transformUpdateRequest(any());

    // when
    restAssuredMockMvcUtils.doPut(updateNotificationConfigRequest);

    // then
    verify(notificationConfigService).updateCurrentUserNotificationConfig(notificationConfigDto);
  }

  private static Stream<Arguments> badRequestInputProvider() {
    UpdateNotificationConfigRequest badRequest1 = UpdateNotificationConfigRequest.builder().frequencyInWeeks(-1).channel("channel").build();
    UpdateNotificationConfigRequest badRequest2 = UpdateNotificationConfigRequest.builder().frequencyInWeeks(0).build();
    UpdateNotificationConfigRequest badRequest3 = UpdateNotificationConfigRequest.builder().frequencyInWeeks(0).channel("").build();
    return Stream.of(
        Arguments.of(badRequest1),
        Arguments.of(badRequest2),
        Arguments.of(badRequest3)
    );
  }
}
