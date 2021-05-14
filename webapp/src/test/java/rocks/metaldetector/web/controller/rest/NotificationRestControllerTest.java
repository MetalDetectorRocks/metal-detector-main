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
import rocks.metaldetector.service.notification.messaging.NotificationScheduler;
import rocks.metaldetector.web.RestAssuredMockMvcUtils;

import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static rocks.metaldetector.support.Endpoints.Rest.NOTIFICATION_ON_ANNOUNCEMENT_DATE;
import static rocks.metaldetector.support.Endpoints.Rest.NOTIFICATION_ON_FREQUENCY;
import static rocks.metaldetector.support.Endpoints.Rest.NOTIFICATION_ON_RELEASE_DATE;

@ExtendWith(MockitoExtension.class)
class NotificationRestControllerTest implements WithAssertions {

  @Mock
  private NotificationScheduler notificationScheduler;

  @InjectMocks
  private NotificationRestController underTest;

  private RestAssuredMockMvcUtils frequencyRestAssuredUtils;
  private RestAssuredMockMvcUtils releaseDateRestAssuredUtils;
  private RestAssuredMockMvcUtils announcementDateRestAssuredUtils;

  @BeforeEach
  void setup() {
    frequencyRestAssuredUtils = new RestAssuredMockMvcUtils(NOTIFICATION_ON_FREQUENCY);
    releaseDateRestAssuredUtils = new RestAssuredMockMvcUtils(NOTIFICATION_ON_RELEASE_DATE);
    announcementDateRestAssuredUtils = new RestAssuredMockMvcUtils(NOTIFICATION_ON_ANNOUNCEMENT_DATE);
    RestAssuredMockMvc.standaloneSetup(underTest);
  }

  @AfterEach
  void tearDown() {
    reset(notificationScheduler);
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
    verify(notificationScheduler).notifyOnFrequency();
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
    verify(notificationScheduler).notifyOnReleaseDate();
  }

  @Test
  @DisplayName("Notify on announcement date responds with OK")
  void notify_on_announcement_date_responds_ok() {
    // when
    var response = announcementDateRestAssuredUtils.doPost();

    // then
    response.statusCode(HttpStatus.OK.value());
  }

  @Test
  @DisplayName("Notify on announcement date calls notification service")
  void notify_on_announcement_date_calls_notification_service() {
    // when
    announcementDateRestAssuredUtils.doPost();

    // then
    verify(notificationScheduler).notifyOnAnnouncementDate();
  }
}
