package rocks.metaldetector.web.controller.mvc.authentication;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import rocks.metaldetector.config.constants.ViewNames;
import rocks.metaldetector.web.RestAssuredMockMvcUtils;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static rocks.metaldetector.support.Endpoints.Authentication.LOGIN;

class LoginControllerTest {

  private final LoginController underTest = new LoginController();

  private RestAssuredMockMvcUtils restAssuredUtils;

  @BeforeEach
  void setup() {
    restAssuredUtils = new RestAssuredMockMvcUtils(LOGIN);
    RestAssuredMockMvc.standaloneSetup(underTest);
  }

  @Test
  @DisplayName("Requesting '" + LOGIN + "' should be ok")
  void given_login_uri_should_return_200() {
    // when
    var validatableResponse = restAssuredUtils.doGet();

    // then
    validatableResponse.assertThat(status().isOk());
  }

  @Test
  @DisplayName("Requesting '" + LOGIN + "' should return the view to login")
  void given_login_uri_should_return_login_view() {
    // when
    var validatableResponse = restAssuredUtils.doGet();

    // then
    validatableResponse.assertThat(view().name(ViewNames.Authentication.LOGIN))
        .assertThat(model().size(0))
        .assertThat(model().hasNoErrors());
  }
}
