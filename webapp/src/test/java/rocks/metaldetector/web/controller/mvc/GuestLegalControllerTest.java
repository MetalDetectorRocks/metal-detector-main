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

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@ExtendWith(MockitoExtension.class)
class GuestLegalControllerTest {

  @InjectMocks
  private GuestLegalController underTest;

  private RestAssuredMockMvcUtils restAssuredUtils;

  @BeforeEach
  void setup() {
    RestAssuredMockMvc.standaloneSetup(underTest);
  }

  @Test
  @DisplayName("GET on /guest/imprint should be ok")
  void given_imprint_uri_then_return_200() {
    // given
    restAssuredUtils = new RestAssuredMockMvcUtils(Endpoints.Guest.IMPRINT);

    // when
    var validatableResponse = restAssuredUtils.doGet();

    // then
    validatableResponse.assertThat(status().isOk());
  }

  @Test
  @DisplayName("GET on /guest/imprint should return view imprint")
  void given_imprint_uri_then_return_imprint_view() {
    // given
    restAssuredUtils = new RestAssuredMockMvcUtils(Endpoints.Guest.IMPRINT);

    // when
    var validatableResponse = restAssuredUtils.doGet();

    // then
    validatableResponse.assertThat(view().name(ViewNames.Guest.IMPRINT))
            .assertThat(model().size(0))
            .assertThat(model().hasNoErrors());
  }

  @Test
  @DisplayName("GET on /guest/privacy-policy should be ok")
  void given_privacy_policy_uri_then_return_200() {
    // given
    restAssuredUtils = new RestAssuredMockMvcUtils(Endpoints.Guest.PRIVACY_POLICY);

    // when
    var validatableResponse = restAssuredUtils.doGet();

    // then
    validatableResponse.assertThat(status().isOk());
  }

  @Test
  @DisplayName("GET on /guest/privacy-policy should return view imprint")
  void given_privacy_policy_uri_then_return_privacy_policy_view() {
    // given
    restAssuredUtils = new RestAssuredMockMvcUtils(Endpoints.Guest.PRIVACY_POLICY);

    // when
    var validatableResponse = restAssuredUtils.doGet();

    // then
    validatableResponse.assertThat(view().name(ViewNames.Guest.PRIVACY_POLICY))
            .assertThat(model().size(0))
            .assertThat(model().hasNoErrors());
  }
}
