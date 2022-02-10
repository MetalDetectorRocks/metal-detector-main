package rocks.metaldetector.web.controller.mvc.authentication;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import rocks.metaldetector.web.RestAssuredMockMvcUtils;
import rocks.metaldetector.web.controller.mvc.PreviewIndexController;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static rocks.metaldetector.config.constants.ViewNames.Authentication.PREVIEW_INDEX;
import static rocks.metaldetector.support.Endpoints.Frontend.HOME;
import static rocks.metaldetector.support.Endpoints.Frontend.INDEX;

class PreviewIndexControllerTest {

  private final PreviewIndexController underTest = new PreviewIndexController();

  private RestAssuredMockMvcUtils restAssuredUtils;

  @BeforeEach
  void setup() {
    RestAssuredMockMvc.standaloneSetup(underTest);
  }

  @ParameterizedTest(name = "GET on <{0}> should be ok")
  @ValueSource(strings = {INDEX, HOME})
  void given_index_uri_then_return_200(String endpoint) {
    // given
    restAssuredUtils = new RestAssuredMockMvcUtils(endpoint);

    // when
    var validatableResponse = restAssuredUtils.doGet();

    // then
    validatableResponse.assertThat(status().isOk());
  }

  @ParameterizedTest(name = "Requesting <{0}> should return the preview index view")
  @ValueSource(strings = {INDEX, HOME})
  void given_index_uri_should_return_preview_index_view(String endpoint) {
    // given
    restAssuredUtils = new RestAssuredMockMvcUtils(endpoint);

    // when
    var validatableResponse = restAssuredUtils.doGet();

    // then
    validatableResponse.assertThat(view().name(PREVIEW_INDEX))
        .assertThat(model().size(0))
        .assertThat(model().hasNoErrors());
  }
}
