package rocks.metaldetector.web.controller.mvc.admin;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import rocks.metaldetector.config.constants.ViewNames;
import rocks.metaldetector.support.Endpoints;
import rocks.metaldetector.web.RestAssuredMockMvcUtils;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@ExtendWith(MockitoExtension.class)
class AdminReleasesControllerTest implements WithAssertions {

  @InjectMocks
  private AdminReleasesController underTest;

  private RestAssuredMockMvcUtils restAssuredUtils;

  @BeforeEach
  void setup() {
    restAssuredUtils = new RestAssuredMockMvcUtils(Endpoints.AdminArea.RELEASES);
    RestAssuredMockMvc.standaloneSetup(underTest);
  }

  @Test
  @DisplayName("Requesting '" + Endpoints.AdminArea.RELEASES + "' should return the releases view")
  void get_search_should_return_releases_view() {
    // when
    var validatableResponse = restAssuredUtils.doGet(MediaType.TEXT_HTML);

    // then
    validatableResponse
        .assertThat(status().isOk())
        .assertThat(view().name(ViewNames.AdminArea.RELEASES))
        .assertThat(model().size(0))
        .assertThat(model().hasNoErrors());
  }
}