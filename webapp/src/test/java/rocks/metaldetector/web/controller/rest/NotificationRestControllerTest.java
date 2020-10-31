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
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@ExtendWith(MockitoExtension.class)
class NotificationRestControllerTest implements WithAssertions {

  @Mock
  private NotificationService notificationService;

  @InjectMocks
  private NotificationRestController underTest;

  private RestAssuredMockMvcUtils restAssuredUtils;

  @BeforeEach
  void setup() {
    restAssuredUtils = new RestAssuredMockMvcUtils(Endpoints.Rest.NOTIFY);
    RestAssuredMockMvc.standaloneSetup(underTest,
                                       springSecurity((request, response, chain) -> chain.doFilter(request, response)));
  }

  @AfterEach
  void tearDown() {
    reset(notificationService);
  }

  @Test
  @DisplayName("Notify all responds with OK")
  void notify_all_responds_ok() {
    // when
    var response = restAssuredUtils.doPost();

    // then
    response.statusCode(HttpStatus.OK.value());
  }

  @Test
  @DisplayName("Notify all calls notification service")
  void notify_all_calls_notification_service() {
    // when
    restAssuredUtils.doPost();

    // then
    verify(notificationService).notifyAllUsers();
  }

  @Test
  @DisplayName("Notify user responds with OK")
  void notify_user_responds_ok() {
    // given
    var userId = "123456";

    // when
    var response = restAssuredUtils.doPost("/" + userId);

    // then
    response.statusCode(HttpStatus.OK.value());
  }

  @Test
  @DisplayName("Notify user calls notification service")
  void notify_user_calls_notification_service() {
    // given
    var userId = "123456";

    // when
    restAssuredUtils.doPost("/" + userId);

    // then
    verify(notificationService).notifyUser(userId);
  }
}