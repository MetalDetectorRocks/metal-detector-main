package rocks.metaldetector.web.controller.mvc;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import rocks.metaldetector.config.constants.ViewNames;
import rocks.metaldetector.support.Endpoints;
import rocks.metaldetector.web.RestAssuredMockMvcUtils;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

class SpotifySynchronizationControllerTest {

  private RestAssuredMockMvcUtils restAssuredUtils;
  private RestAssuredMockMvcUtils callbackRestAssuredUtils;

  @BeforeEach
  void setup() {
    restAssuredUtils = new RestAssuredMockMvcUtils(Endpoints.Frontend.SPOTIFY_SYNCHRONIZATION);
    callbackRestAssuredUtils = new RestAssuredMockMvcUtils(Endpoints.Frontend.SPOTIFY_SYNCHRONIZATION + Endpoints.Frontend.SPOTIFY_CALLBACK);
    RestAssuredMockMvc.standaloneSetup(new SpotifySynchronizationController());
  }

  @Test
  @DisplayName("Requesting '" + Endpoints.Frontend.SPOTIFY_SYNCHRONIZATION + "' should be ok")
  void get_search_should_return_200() {
    // when
    var validatableResponse = restAssuredUtils.doGet();

    // then
    validatableResponse.assertThat(status().isOk());
  }

  @Test
  @DisplayName("Requesting '" + Endpoints.Frontend.SPOTIFY_SYNCHRONIZATION + "' should return the spotify synchronization view")
  void get_search_should_return_profile_view() {
    // when
    var validatableResponse = restAssuredUtils.doGet();

    // then
    validatableResponse.assertThat(view().name(ViewNames.Frontend.SPOTIFY_SYNCHRONIZATION))
        .assertThat(model().size(0))
        .assertThat(model().hasNoErrors());
  }

  @Test
  @DisplayName("Requesting '" + Endpoints.Frontend.SPOTIFY_SYNCHRONIZATION + Endpoints.Frontend.SPOTIFY_CALLBACK + "' should be ok")
  void get_callback_should_return_200() {
    // when
    var validatableResponse = callbackRestAssuredUtils.doGet();

    // then
    validatableResponse.assertThat(status().isOk());
  }

  @Test
  @DisplayName("Requesting '" + Endpoints.Frontend.SPOTIFY_SYNCHRONIZATION + Endpoints.Frontend.SPOTIFY_CALLBACK + "' should return the spotify synchronization view")
  void get_callback_should_return_profile_view() {
    // when
    var validatableResponse = callbackRestAssuredUtils.doGet();

    // then
    validatableResponse.assertThat(view().name(ViewNames.Frontend.SPOTIFY_SYNCHRONIZATION))
        .assertThat(model().size(0))
        .assertThat(model().hasNoErrors());
  }
}
