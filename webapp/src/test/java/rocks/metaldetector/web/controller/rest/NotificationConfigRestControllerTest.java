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
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import rocks.metaldetector.service.notification.NotificationConfigDto;
import rocks.metaldetector.service.notification.NotificationService;
import rocks.metaldetector.support.Endpoints;
import rocks.metaldetector.web.RestAssuredMockMvcUtils;
import rocks.metaldetector.web.api.request.UpdateNotificationConfigRequest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

@ExtendWith(MockitoExtension.class)
class NotificationConfigRestControllerTest implements WithAssertions {

  @Mock
  private NotificationService notificationService;

  @Spy
  private ModelMapper modelMapper;

  @InjectMocks
  private NotificationConfigRestController underTest;

  private RestAssuredMockMvcUtils restAssuredMockMvcUtils;

  @BeforeEach
  void setup() {
    restAssuredMockMvcUtils = new RestAssuredMockMvcUtils(Endpoints.Rest.NOTIFICATION_CONFIG);
    RestAssuredMockMvc.standaloneSetup(underTest);
  }

  @AfterEach
  void tearDown() {
    reset(notificationService, modelMapper);
  }

  @Test
  @DisplayName("Getting the current user's notification config should return 200")
  void test_get_returns_200() {
    // when
    var validatableResponse = restAssuredMockMvcUtils.doGet();

    // then
    validatableResponse.statusCode(OK.value());
  }

  @Test
  @DisplayName("Getting the current user's notification config calls service")
  void test_get_calls_service() {
    // when
    restAssuredMockMvcUtils.doGet();

    // then
    verify(notificationService).getCurrentUserNotificationConfig();
  }

  @Test
  @DisplayName("Getting the current user's notification config returns dto")
  void test_get_returns_dto() {
    // given
    var expectedDto = NotificationConfigDto.builder().frequencyInWeeks(4).build();
    doReturn(expectedDto).when(notificationService).getCurrentUserNotificationConfig();

    // when
    var validatableResponse = restAssuredMockMvcUtils.doGet();

    // then
    var response = validatableResponse.extract().as(NotificationConfigDto.class);
    assertThat(response).isEqualTo(expectedDto);
  }

  @Test
  @DisplayName("Updating the current user's notification config should return 200")
  void test_put_returns_200() {
    // given
    var updateNotificationConfigRequest = UpdateNotificationConfigRequest.builder().frequencyInWeeks(4).build();

    // when
    var validatableResponse = restAssuredMockMvcUtils.doPut(updateNotificationConfigRequest);

    // then
    validatableResponse.statusCode(OK.value());
  }

  @Test
  @DisplayName("Updating the current user's notification config should return 400")
  void test_put_returns_400() {
    // given
    var updateNotificationConfigRequest = UpdateNotificationConfigRequest.builder().frequencyInWeeks(-1).build();

    // when
    var validatableResponse = restAssuredMockMvcUtils.doPut(updateNotificationConfigRequest);

    // then
    validatableResponse.statusCode(BAD_REQUEST.value());
  }

  @Test
  @DisplayName("Updating the current user's notification config calls mapper")
  void test_put_calls_mapper() {
    // given
    var updateNotificationConfigRequest = UpdateNotificationConfigRequest.builder().frequencyInWeeks(4).build();

    // when
    restAssuredMockMvcUtils.doPut(updateNotificationConfigRequest);

    // then
    verify(modelMapper).map(eq(updateNotificationConfigRequest), eq(NotificationConfigDto.class));
  }

  @Test
  @DisplayName("Updating the current user's notification config calls service")
  void test_put_calls_service() {
    // given
    var updateNotificationConfigRequest = UpdateNotificationConfigRequest.builder().frequencyInWeeks(4).build();
    var notificationConfigDto = NotificationConfigDto.builder().frequencyInWeeks(4).build();
    doReturn(notificationConfigDto).when(modelMapper).map(any(), any());

    // when
    restAssuredMockMvcUtils.doPut(updateNotificationConfigRequest);

    // then
    verify(notificationService).updateCurrentUserNotificationConfig(notificationConfigDto);
  }
}