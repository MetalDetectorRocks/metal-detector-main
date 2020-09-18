package rocks.metaldetector.web.controller.mvc;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import rocks.metaldetector.config.constants.ViewNames;
import rocks.metaldetector.support.Endpoints;
import rocks.metaldetector.web.RestAssuredMockMvcUtils;

import java.util.Map;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@ExtendWith(MockitoExtension.class)
class SpotifyUserAuthorizationControllerTest {

  @InjectMocks
  private SpotifyUserAuthorizationController underTest;

  private RestAssuredMockMvcUtils restAssuredUtils;

  @BeforeEach
  void setup() {
    restAssuredUtils = new RestAssuredMockMvcUtils(Endpoints.Frontend.PROFILE + Endpoints.Frontend.AUTHORIZE);
    RestAssuredMockMvc.standaloneSetup(underTest,
                                       springSecurity((request, response, chain) -> chain.doFilter(request, response)));
  }

  @Test
  @DisplayName("Requesting '" + Endpoints.Frontend.PROFILE + Endpoints.Frontend.AUTHORIZE + "' should be ok")
  void get_import_should_return_200() {
    // given
    Map<String, Object> parameter = Map.of("code", "code", "state", "state");

    // when
    var validatableResponse = restAssuredUtils.doGet(parameter);

    // then
    validatableResponse.assertThat(status().isOk());
  }

  @Test
  @DisplayName("Requesting '" + Endpoints.Frontend.PROFILE + Endpoints.Frontend.AUTHORIZE + "' should return the profile view")
  void get_import_search_should_return_my_artists_view() {
    // given
    Map<String, Object> parameter = Map.of("code", "code", "state", "state");

    // when
    var validatableResponse = restAssuredUtils.doGet(parameter);

    // then
    validatableResponse.assertThat(view().name(ViewNames.Frontend.PROFILE))
        .assertThat(model().size(0))
        .assertThat(model().hasNoErrors());
  }
}
