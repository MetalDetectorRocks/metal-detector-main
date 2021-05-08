package rocks.metaldetector.web.controller.rest;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import rocks.metaldetector.web.RestAssuredMockMvcUtils;

import static rocks.metaldetector.support.Endpoints.Rest.NOTIFICATION_ON_ANNOUNCEMENT_DATE;
import static rocks.metaldetector.support.Endpoints.Rest.NOTIFICATION_ON_FREQUENCY;
import static rocks.metaldetector.support.Endpoints.Rest.NOTIFICATION_ON_RELEASE_DATE;

class NoOpNotificationRestControllerTest implements WithAssertions {

  private RestAssuredMockMvcUtils frequencyRestAssuredUtils;
  private RestAssuredMockMvcUtils releaseDateRestAssuredUtils;
  private RestAssuredMockMvcUtils announcementDateRestAssuredUtils;

  @BeforeEach
  void setup() {
    frequencyRestAssuredUtils = new RestAssuredMockMvcUtils(NOTIFICATION_ON_FREQUENCY);
    releaseDateRestAssuredUtils = new RestAssuredMockMvcUtils(NOTIFICATION_ON_RELEASE_DATE);
    announcementDateRestAssuredUtils = new RestAssuredMockMvcUtils(NOTIFICATION_ON_ANNOUNCEMENT_DATE);
    RestAssuredMockMvc.standaloneSetup(new NoOpNotificationRestController());
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
  @DisplayName("Notify on release date responds with OK")
  void notify_on_release_date_responds_ok() {
    // when
    var response = releaseDateRestAssuredUtils.doPost();

    // then
    response.statusCode(HttpStatus.OK.value());
  }

  @Test
  @DisplayName("Notify on announcement date responds with OK")
  void notify_on_announcement_date_responds_ok() {
    // when
    var response = announcementDateRestAssuredUtils.doPost();

    // then
    response.statusCode(HttpStatus.OK.value());
  }
}
