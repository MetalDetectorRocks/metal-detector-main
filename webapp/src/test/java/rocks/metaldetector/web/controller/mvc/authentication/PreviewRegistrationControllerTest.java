package rocks.metaldetector.web.controller.mvc.authentication;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import rocks.metaldetector.web.RestAssuredMockMvcUtils;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static rocks.metaldetector.config.constants.ViewNames.Authentication.DISABLED_REGISTER;
import static rocks.metaldetector.support.Endpoints.Authentication.REGISTER;

@ExtendWith(MockitoExtension.class)
class PreviewRegistrationControllerTest implements WithAssertions {

  private final PreviewRegistrationController underTest = new PreviewRegistrationController();
  private RestAssuredMockMvcUtils restAssuredUtils;

  @BeforeEach
  void setup() {
    restAssuredUtils = new RestAssuredMockMvcUtils(REGISTER);
    RestAssuredMockMvc.standaloneSetup(underTest);
  }

  @Test
  @DisplayName("Requesting '" + REGISTER + "' should return the view to register")
  void given_register_uri_should_return_register_view() {
    // when
    var validatableResponse = restAssuredUtils.doGet();

    // then
    validatableResponse
        .assertThat(status().isOk())
        .assertThat(view().name(DISABLED_REGISTER));
  }
}
