package rocks.metaldetector.web.controller.mvc;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import rocks.metaldetector.config.constants.ViewNames;
import rocks.metaldetector.support.Endpoints;
import rocks.metaldetector.web.RestAssuredMockMvcUtils;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@ExtendWith(MockitoExtension.class)
class IndexControllerTest {

  @InjectMocks
  private IndexController underTest;

  private RestAssuredMockMvcUtils restAssuredUtils;

  @BeforeEach
  void setup() {
    RestAssuredMockMvc.standaloneSetup(underTest);
  }

  @ParameterizedTest(name = "[{index}] => Endpoint <{0}>")
  @ValueSource(strings = {Endpoints.Guest.INDEX, Endpoints.Guest.SLASH_INDEX})
  @DisplayName("GET on index should be ok")
  void given_index_uri_then_return_200(String endpoint) {
    // given
    restAssuredUtils = new RestAssuredMockMvcUtils(endpoint);

    // when
    var validatableResponse = restAssuredUtils.doGet();

    // then
    validatableResponse.assertThat(status().isOk());
  }

  @ParameterizedTest(name = "[{index}] => Endpoint <{0}>")
  @ValueSource(strings = {Endpoints.Guest.INDEX, Endpoints.Guest.SLASH_INDEX})
  @DisplayName("GET on index should return index view")
  void given_index_uri_then_return_index_view(String endpoint) {
    // given
    restAssuredUtils = new RestAssuredMockMvcUtils(endpoint);

    // when
    var validatableResponse = restAssuredUtils.doGet();

    // then
    validatableResponse.assertThat(view().name(ViewNames.Frontend.INDEX))
        .assertThat(model().size(0))
        .assertThat(model().hasNoErrors());
  }
}
