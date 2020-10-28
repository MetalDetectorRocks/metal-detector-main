package rocks.metaldetector.web.controller.mvc;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rocks.metaldetector.config.constants.ViewNames;
import rocks.metaldetector.service.spotify.SpotifyUserAuthorizationService;
import rocks.metaldetector.support.Endpoints;
import rocks.metaldetector.web.RestAssuredMockMvcUtils;

import java.util.Map;

import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@ExtendWith(MockitoExtension.class)
class ProfileControllerTest {

  @Mock
  private SpotifyUserAuthorizationService userAuthorizationService;

  @InjectMocks
  private ProfileController underTest;

  private RestAssuredMockMvcUtils restAssuredUtils;
  private RestAssuredMockMvcUtils callbackRestAssuredUtils;

  @BeforeEach
  void setup() {
    restAssuredUtils = new RestAssuredMockMvcUtils(Endpoints.Frontend.PROFILE);
    callbackRestAssuredUtils = new RestAssuredMockMvcUtils(Endpoints.Frontend.PROFILE + Endpoints.Frontend.SPOTIFY_CALLBACK);
    RestAssuredMockMvc.standaloneSetup(underTest,
                                       springSecurity((request, response, chain) -> chain.doFilter(request, response)));
  }

  @AfterEach
  void tearDown() {
    reset(userAuthorizationService);
  }

  @Test
  @DisplayName("Requesting '" + Endpoints.Frontend.PROFILE + "' should be ok")
  void get_search_should_return_200() {
    // when
    var validatableResponse = restAssuredUtils.doGet();

    // then
    validatableResponse.assertThat(status().isOk());
  }

  @Test
  @DisplayName("Requesting '" + Endpoints.Frontend.PROFILE + "' should return the profile view")
  void get_search_should_return_profile_view() {
    // when
    var validatableResponse = restAssuredUtils.doGet();

    // then
    validatableResponse.assertThat(view().name(ViewNames.Frontend.PROFILE))
        .assertThat(model().size(0))
        .assertThat(model().hasNoErrors());
  }

  @Test
  @DisplayName("Requesting '" + Endpoints.Frontend.PROFILE + Endpoints.Frontend.SPOTIFY_CALLBACK + "' should be ok")
  void get_callback_should_return_200() {
    // given
    Map<String, Object> parameter = Map.of("code", "code", "state", "state");

    // when
    var validatableResponse = callbackRestAssuredUtils.doGet(parameter);

    // then
    validatableResponse.assertThat(status().isOk());
  }

  @Test
  @DisplayName("Requesting '" + Endpoints.Frontend.PROFILE + Endpoints.Frontend.SPOTIFY_CALLBACK + "' should return the profile view")
  void get_callback_should_return_profile_view() {
    // given
    Map<String, Object> parameter = Map.of("code", "code", "state", "state");

    // when
    var validatableResponse = callbackRestAssuredUtils.doGet(parameter);

    // then
    validatableResponse.assertThat(view().name(ViewNames.Frontend.PROFILE))
        .assertThat(model().size(0))
        .assertThat(model().hasNoErrors());
  }

  @Test
  @DisplayName("spotifyUserAuthorizationService is called")
  void test_authorization_service_called() {
    // given
    var code = "code";
    var state = "state";
    Map<String, Object> parameter = Map.of("code", code, "state", state);

    // when
    callbackRestAssuredUtils.doGet(parameter);

    // then
    verify(userAuthorizationService, times(1)).fetchInitialToken(state, code);
  }
}
