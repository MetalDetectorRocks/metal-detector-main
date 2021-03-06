package rocks.metaldetector.web.controller.mvc.admin;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import io.restassured.module.mockmvc.response.ValidatableMockMvcResponse;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.ModelAndView;
import rocks.metaldetector.config.constants.ViewNames;
import rocks.metaldetector.support.Endpoints;
import rocks.metaldetector.web.RestAssuredMockMvcUtils;

@ExtendWith(MockitoExtension.class)
class AdminUserControllerTest implements WithAssertions {

  @InjectMocks
  private AdminUserController underTest;

  private RestAssuredMockMvcUtils restAssuredUtils;

  @BeforeEach
  void setup() {
    restAssuredUtils = new RestAssuredMockMvcUtils(Endpoints.AdminArea.USERS);
    RestAssuredMockMvc.standaloneSetup(underTest);
  }

  @Test
  @DisplayName("Requesting '" + Endpoints.AdminArea.USERS + "' should return the releases view")
  void get_search_should_return_releases_view() {
    // when
    ValidatableMockMvcResponse response = restAssuredUtils.doGet(MediaType.TEXT_HTML);

    // then
    response.statusCode(HttpStatus.OK.value());

    ModelAndView modelAndView = response.extract().response().getMvcResult().getModelAndView();

    assertThat(modelAndView).isNotNull();
    assertThat(modelAndView.getViewName()).isEqualTo(ViewNames.AdminArea.USERS);
    assertThat(modelAndView.getModel()).isEmpty();
  }
}