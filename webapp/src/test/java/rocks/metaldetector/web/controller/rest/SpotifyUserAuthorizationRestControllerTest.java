package rocks.metaldetector.web.controller.rest;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import rocks.metaldetector.service.spotify.SpotifyUserAuthorizationService;
import rocks.metaldetector.support.Endpoints;
import rocks.metaldetector.web.RestAssuredMockMvcUtils;
import rocks.metaldetector.web.api.response.SpotifyUserAuthorizationResponse;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@ExtendWith(MockitoExtension.class)
class SpotifyUserAuthorizationRestControllerTest implements WithAssertions {

  @Mock
  private SpotifyUserAuthorizationService spotifyUserAuthorizationServiceService;

  @InjectMocks
  private SpotifyUserAuthorizationRestController underTest;

  private RestAssuredMockMvcUtils restAssuredMockMvcUtils;

  @BeforeEach
  void setup() {
    restAssuredMockMvcUtils = new RestAssuredMockMvcUtils(Endpoints.Rest.SPOTIFY_AUTHORIZATION);
    RestAssuredMockMvc.standaloneSetup(underTest,
                                       springSecurity((request, response, chain) -> chain.doFilter(request, response)));
  }

  @AfterEach
  void tearDown() {
    reset(spotifyUserAuthorizationServiceService);
  }

  @Test
  @DisplayName("GET on " + Endpoints.Rest.SPOTIFY_AUTHORIZATION + " should return 200")
  void test_get_import_returns_ok() {
    // when
    var validatableResponse = restAssuredMockMvcUtils.doGet();

    // then
    validatableResponse.statusCode(HttpStatus.OK.value());
  }

  @Test
  @DisplayName("GET on " + Endpoints.Rest.SPOTIFY_AUTHORIZATION + " should call SpotifyUserAuthorizationServiceService")
  void test_get_import_calls_spotify_service() {
    // when
    restAssuredMockMvcUtils.doGet();

    // then
    verify(spotifyUserAuthorizationServiceService, times(1)).prepareAuthorization();
  }

  @Test
  @DisplayName("GET on " + Endpoints.Rest.SPOTIFY_AUTHORIZATION + " should return expected url")
  void test_get_import_returns_url() {
    // given
    var expectedUrl = "i'm an url";
    doReturn(expectedUrl).when(spotifyUserAuthorizationServiceService).prepareAuthorization();

    // when
    var validatableResponse = restAssuredMockMvcUtils.doGet();

    // then
    var response = validatableResponse.extract().as(SpotifyUserAuthorizationResponse.class);
    assertThat(response.getAuthorizationUrl()).isEqualTo(expectedUrl);
  }
}
