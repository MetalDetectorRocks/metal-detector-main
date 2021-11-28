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
import rocks.metaldetector.security.CurrentUserSupplier;
import rocks.metaldetector.service.user.UserEntityFactory;
import rocks.metaldetector.support.Endpoints;
import rocks.metaldetector.web.RestAssuredMockMvcUtils;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@ExtendWith(MockitoExtension.class)
class DashboardControllerTest {

  @Mock
  private CurrentUserSupplier currentUserSupplier;

  @InjectMocks
  private DashboardController underTest;

  private RestAssuredMockMvcUtils restAssuredUtils;

  @BeforeEach
  void setup() {
    restAssuredUtils = new RestAssuredMockMvcUtils(Endpoints.Frontend.DASHBOARD);
    RestAssuredMockMvc.standaloneSetup(underTest);
    doReturn(UserEntityFactory.createUser("user", "mail@test.de")).when(currentUserSupplier).get();
  }

  @AfterEach
  void tearDown() {
    reset(currentUserSupplier);
  }

  @Test
  @DisplayName("Requesting '" + Endpoints.Frontend.DASHBOARD + "' should be ok")
  void get_should_return_200() {
    // when
    var validatableResponse = restAssuredUtils.doGet();

    // then
    validatableResponse.assertThat(status().isOk());
  }

  @Test
  @DisplayName("Requesting '" + Endpoints.Frontend.DASHBOARD + "' should return the home view")
  void get_should_return_home_view() {
    // when
    var validatableResponse = restAssuredUtils.doGet();

    // then
    validatableResponse.assertThat(view().name(ViewNames.Frontend.DASHBOARD))
        .assertThat(model().size(1))
        .assertThat(model().hasNoErrors());
  }

  @Test
  @DisplayName("Requesting '" + Endpoints.Frontend.DASHBOARD + "' should return the model containing the username")
  void get_should_return_correct_model() {
    // when
    var validatableResponse = restAssuredUtils.doGet();

    // then
    validatableResponse.assertThat(view().name(ViewNames.Frontend.DASHBOARD))
        .assertThat(model().size(1))
        .assertThat(model().attribute("username", "user"))
        .assertThat(model().hasNoErrors());
  }

  @Test
  @DisplayName("Requesting '" + Endpoints.Frontend.DASHBOARD + "' should call CurrentUserSupplier")
  void test_current_user_supplier_called() {
    // when
    restAssuredUtils.doGet();

    // then
    verify(currentUserSupplier).get();
  }
}
