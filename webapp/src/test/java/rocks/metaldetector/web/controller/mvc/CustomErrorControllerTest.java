package rocks.metaldetector.web.controller.mvc;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import rocks.metaldetector.config.constants.ViewNames;
import rocks.metaldetector.support.Endpoints;
import rocks.metaldetector.web.RestAssuredMockMvcUtils;

import javax.servlet.RequestDispatcher;
import java.util.Map;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@ExtendWith(MockitoExtension.class)
class CustomErrorControllerTest {

  @InjectMocks
  private CustomErrorController underTest;

  private RestAssuredMockMvcUtils restAssuredUtils;

  @BeforeEach
  void setup() {
    restAssuredUtils = new RestAssuredMockMvcUtils(Endpoints.ERROR);
    RestAssuredMockMvc.standaloneSetup(underTest);
  }

  @Test
  @DisplayName("Return 404 if no controller for the requested URI was found")
  void test_error_404_status() {
    // when
    var validatableResponse = restAssuredUtils.doGetWithAttributes(Map.of(RequestDispatcher.ERROR_STATUS_CODE, HttpStatus.NOT_FOUND.value(),
                                                                          RequestDispatcher.ERROR_REQUEST_URI, "/not-existing"));

    // then
    validatableResponse.assertThat(status().isNotFound());
  }

  @Test
  @DisplayName("Return correct view if no controller for the requested URI was found")
  void test_error_404_view() {
    //given
    final String REQUEST_URI = "/not-existing";

    // when
    var validatableResponse = restAssuredUtils.doGetWithAttributes(Map.of(RequestDispatcher.ERROR_STATUS_CODE, HttpStatus.NOT_FOUND.value(),
                                                                          RequestDispatcher.ERROR_REQUEST_URI, REQUEST_URI));

    // then
    validatableResponse.assertThat(model().attribute("requestedURI", REQUEST_URI))
        .assertThat(view().name(ViewNames.Guest.ERROR_404));
  }

  @Test
  @DisplayName("Return 403 if the access for the requested URI is denied")
  void test_error_403_status() {
    // given
    final String REQUEST_URI = "/not-allowed";

    // when
    var validatableResponse = restAssuredUtils.doGetWithAttributes(Map.of(RequestDispatcher.ERROR_STATUS_CODE, HttpStatus.FORBIDDEN.value(),
                                                                          RequestDispatcher.ERROR_REQUEST_URI, REQUEST_URI));

    // then
    validatableResponse.assertThat(status().isForbidden());
  }

  @Test
  @DisplayName("Return correct view if the access for the requested URI is denied")
  void test_error_403_view() {
    // given
    final String REQUEST_URI = "/not-allowed";

    // when
    var validatableResponse = restAssuredUtils.doGetWithAttributes(Map.of(RequestDispatcher.ERROR_STATUS_CODE, HttpStatus.FORBIDDEN.value(),
                                                                          RequestDispatcher.ERROR_REQUEST_URI, REQUEST_URI));

    // then
    validatableResponse.assertThat(model().attribute("requestedURI", REQUEST_URI))
        .assertThat(view().name(ViewNames.Guest.ERROR_403));
  }

  @Test
  @DisplayName("Return 500 in case of unhandled server error")
  void test_error_500_status() {
    // given
    final String REQUEST_URI = "/server-error";

    // when
    var validatableResponse = restAssuredUtils.doGetWithAttributes(Map.of(RequestDispatcher.ERROR_STATUS_CODE, HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                                                          RequestDispatcher.ERROR_REQUEST_URI, REQUEST_URI));

    // then
    validatableResponse.assertThat(status().isInternalServerError());
  }

  @Test
  @DisplayName("Return correct view in case of unhandled server error")
  void test_error_500_view() {
    // given
    final String REQUEST_URI = "/server-error";

    // when
    var validatableResponse = restAssuredUtils.doGetWithAttributes(Map.of(RequestDispatcher.ERROR_STATUS_CODE, HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                                                          RequestDispatcher.ERROR_REQUEST_URI, REQUEST_URI));

    // then
    validatableResponse.assertThat(model().attribute("requestedURI", REQUEST_URI))
        .assertThat(view().name(ViewNames.Guest.ERROR_500));
  }

  @Test
  @DisplayName("Return general error page for all other errors")
  void test_all_other_errors() {
    // given
    final String REQUEST_URI = "/all-other-errors";

    // when
    var validatableResponse = restAssuredUtils.doGetWithAttributes(Map.of(RequestDispatcher.ERROR_STATUS_CODE, HttpStatus.BAD_GATEWAY.value(),
                                                                          RequestDispatcher.ERROR_REQUEST_URI, REQUEST_URI));

    // then
    validatableResponse.assertThat(status().isBadGateway())
        .assertThat(model().attribute("requestedURI", REQUEST_URI))
        .assertThat(view().name(ViewNames.Guest.ERROR));
  }
}
