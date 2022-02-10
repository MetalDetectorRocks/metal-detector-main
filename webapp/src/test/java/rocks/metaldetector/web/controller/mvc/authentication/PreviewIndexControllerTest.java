package rocks.metaldetector.web.controller.mvc.authentication;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rocks.metaldetector.security.AuthenticationFacade;
import rocks.metaldetector.service.user.UserEntityFactory;
import rocks.metaldetector.web.RestAssuredMockMvcUtils;
import rocks.metaldetector.web.controller.mvc.PreviewIndexController;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static rocks.metaldetector.config.constants.ViewNames.Authentication.PREVIEW_INDEX;
import static rocks.metaldetector.config.constants.ViewNames.Frontend.DASHBOARD;
import static rocks.metaldetector.support.Endpoints.Frontend.HOME;
import static rocks.metaldetector.support.Endpoints.Frontend.INDEX;

@ExtendWith(MockitoExtension.class)
class PreviewIndexControllerTest {

  @Mock
  private AuthenticationFacade authenticationFacade;

  @InjectMocks
  private PreviewIndexController underTest;

  private RestAssuredMockMvcUtils restAssuredUtils;

  @BeforeEach
  void setup() {
    restAssuredUtils = new RestAssuredMockMvcUtils(HOME);
    RestAssuredMockMvc.standaloneSetup(underTest);
    doReturn(UserEntityFactory.createUser("user", "mail@example.com")).when(authenticationFacade).getCurrentUser();
  }

  @AfterEach
  void tearDown() {
    reset(authenticationFacade);
  }

  @ParameterizedTest(name = "GET on <{0}> should be ok if user is not authenticated")
  @ValueSource(strings = {INDEX, HOME})
  void given_index_uri_then_return_200(String endpoint) {
    // given
    restAssuredUtils = new RestAssuredMockMvcUtils(endpoint);
    doReturn(false).when(authenticationFacade).isAuthenticated();

    // when
    var validatableResponse = restAssuredUtils.doGet();

    // then
    validatableResponse.assertThat(status().isOk());
  }

  @Test
  @DisplayName("should get current user from authentication facade if user is authenticated")
  void should_get_current_user() {
    // given
    doReturn(true).when(authenticationFacade).isAuthenticated();

    // when
    restAssuredUtils.doGet();

    // then
    verify(authenticationFacade).getCurrentUser();
  }

  @ParameterizedTest(name = "Requesting <{0}> should return the preview index view if user is not authenticated")
  @ValueSource(strings = {INDEX, HOME})
  void given_index_uri_should_return_preview_index_view(String endpoint) {
    // given
    restAssuredUtils = new RestAssuredMockMvcUtils(endpoint);
    doReturn(false).when(authenticationFacade).isAuthenticated();

    // when
    var validatableResponse = restAssuredUtils.doGet();

    // then
    validatableResponse.assertThat(view().name(PREVIEW_INDEX))
        .assertThat(model().size(0))
        .assertThat(model().hasNoErrors());
  }

  @Test
  @DisplayName("should return the dashboard view if user is authenticated")
  void should_return_dashboard_view() {
    // given
    doReturn(true).when(authenticationFacade).isAuthenticated();

    // when
    var validatableResponse = restAssuredUtils.doGet();

    // then
    validatableResponse.assertThat(view().name(DASHBOARD))
        .assertThat(model().size(1))
        .assertThat(model().hasNoErrors());
  }

  @Test
  @DisplayName("should return the model containing the username for dashboard view")
  void get_should_return_correct_model() {
    // given
    doReturn(true).when(authenticationFacade).isAuthenticated();

    // when
    var validatableResponse = restAssuredUtils.doGet();

    // then
    validatableResponse.assertThat(view().name(DASHBOARD))
        .assertThat(model().size(1))
        .assertThat(model().attribute("username", "user"))
        .assertThat(model().hasNoErrors());
  }
}
