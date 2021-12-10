package rocks.metaldetector.web.controller.mvc;

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
import rocks.metaldetector.support.Endpoints;
import rocks.metaldetector.web.RestAssuredMockMvcUtils;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static rocks.metaldetector.config.constants.ViewNames.Frontend.DASHBOARD;
import static rocks.metaldetector.config.constants.ViewNames.Frontend.INDEX;
import static rocks.metaldetector.support.Endpoints.Frontend.SLASH_HOME;

@ExtendWith(MockitoExtension.class)
class IndexControllerTest {

  @Mock
  private AuthenticationFacade authenticationFacade;

  @InjectMocks
  private IndexController underTest;

  private RestAssuredMockMvcUtils restAssuredUtils;

  @BeforeEach
  void setup() {
    restAssuredUtils = new RestAssuredMockMvcUtils(SLASH_HOME);
    RestAssuredMockMvc.standaloneSetup(underTest);
    doReturn(UserEntityFactory.createUser("user", "mail@test.de")).when(authenticationFacade).getCurrentUser();
  }

  @AfterEach
  void tearDown() {
    reset(authenticationFacade);
  }

  @ParameterizedTest(name = "[{index}] => Endpoint <{0}>")
  @ValueSource(strings = {Endpoints.Frontend.INDEX, SLASH_HOME})
  @DisplayName("GET on index should be ok")
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
  @DisplayName("should call authentication facade")
  void should_call_authentication_facade() {
    // given
    doReturn(true).when(authenticationFacade).isAuthenticated();

    // when
    restAssuredUtils.doGet();

    // then
    verify(authenticationFacade).isAuthenticated();
    verify(authenticationFacade).getCurrentUser();
  }

  @ParameterizedTest(name = "[{index}] => Endpoint <{0}>")
  @ValueSource(strings = {Endpoints.Frontend.INDEX, SLASH_HOME})
  @DisplayName("should return the home/index view for unauthenticated user")
  void given_index_uri_then_return_index_view(String endpoint) {
    // given
    restAssuredUtils = new RestAssuredMockMvcUtils(endpoint);
    doReturn(false).when(authenticationFacade).isAuthenticated();

    // when
    var validatableResponse = restAssuredUtils.doGet();

    // then
    validatableResponse.assertThat(view().name(INDEX))
        .assertThat(model().size(0))
        .assertThat(model().hasNoErrors());
  }

  @Test
  @DisplayName("should return the dashboard view for authenticated user")
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
