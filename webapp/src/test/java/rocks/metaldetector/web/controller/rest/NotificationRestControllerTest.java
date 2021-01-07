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
import org.springframework.http.HttpStatus;
import rocks.metaldetector.service.notification.NotificationService;
import rocks.metaldetector.support.Endpoints;
import rocks.metaldetector.web.RestAssuredMockMvcUtils;

import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NotificationRestControllerTest implements WithAssertions {

  @Mock
  private NotificationService notificationService;

  @InjectMocks
  private NotificationRestController underTest;

  private RestAssuredMockMvcUtils frequencyRestAssuredUtils;
  private RestAssuredMockMvcUtils releaseDateRestAssuredUtils;

  @BeforeEach
  void setup() {
    frequencyRestAssuredUtils = new RestAssuredMockMvcUtils(Endpoints.Rest.NOTIFY + Endpoints.Rest.FREQUENCY);
    releaseDateRestAssuredUtils = new RestAssuredMockMvcUtils(Endpoints.Rest.NOTIFY + Endpoints.Rest.RELEASE_DATE);
    RestAssuredMockMvc.standaloneSetup(underTest);
  }

  @AfterEach
  void tearDown() {
    reset(notificationService);
  }

  @Test
  @DisplayName("Notify on frequency responds with OK")
  void notify_frequency_responds_ok() {
    // when
    var response = frequencyRestAssuredUtils.doPost();

    // then
    response.statusCode(HttpStatus.OK.value());
  }

  @Test
  @DisplayName("Notify on frequency calls notification service")
  void notify_frequency_calls_notification_service() {
    // when
    frequencyRestAssuredUtils.doPost();

    // then
    verify(notificationService).notifyOnFrequency();
  }

  @Test
  @DisplayName("Notify on release date responds with OK")
  void notify_on_release_date_responds_ok() {
    // when
    var response = releaseDateRestAssuredUtils.doPost();

    // then
    response.statusCode(HttpStatus.OK.value());
  }

  @Test
  @DisplayName("Notify on release date calls notification service")
  void notify_on_release_date_calls_notification_service() {
    // when
    releaseDateRestAssuredUtils.doPost();

    // then
    verify(notificationService).notifyOnReleaseDate();
  }
}