package rocks.metaldetector.web.controller.mvc.authentication;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import rocks.metaldetector.config.constants.Endpoints;
import rocks.metaldetector.config.constants.ViewNames;
import rocks.metaldetector.testutil.WithExceptionResolver;
import rocks.metaldetector.web.RestAssuredMockMvcUtils;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@ExtendWith(MockitoExtension.class)
class PreviewRegistrationControllerTest implements WithAssertions, WithExceptionResolver {

  private PreviewRegistrationController underTest = new PreviewRegistrationController();
  private RestAssuredMockMvcUtils restAssuredUtils;

  @BeforeEach
  void setup() {
    restAssuredUtils = new RestAssuredMockMvcUtils(Endpoints.Guest.REGISTER);
    RestAssuredMockMvc.standaloneSetup(
            underTest,
            springSecurity((request, response, chain) -> chain.doFilter(request, response))
    );
  }

  @Test
  @DisplayName("Requesting '" + Endpoints.Guest.REGISTER + "' should return the view to register")
  void given_register_uri_should_return_register_view() {
    // when
    var validatableResponse = restAssuredUtils.doGet();

    // then
    validatableResponse
            .assertThat(status().isOk())
            .assertThat(view().name(ViewNames.Guest.DISABLED_REGISTER));
  }
}
