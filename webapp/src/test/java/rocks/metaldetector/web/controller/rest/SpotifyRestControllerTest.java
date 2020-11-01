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
import rocks.metaldetector.service.spotify.SpotifyArtistImportService;
import rocks.metaldetector.service.spotify.SpotifyUserAuthorizationService;
import rocks.metaldetector.support.Endpoints;
import rocks.metaldetector.testutil.DtoFactory.ArtistDtoFactory;
import rocks.metaldetector.web.RestAssuredMockMvcUtils;
import rocks.metaldetector.web.api.response.SpotifyArtistImportResponse;
import rocks.metaldetector.web.api.response.SpotifyUserAuthorizationResponse;

import java.util.List;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@ExtendWith(MockitoExtension.class)
class SpotifyRestControllerTest implements WithAssertions {

  @Mock
  private SpotifyUserAuthorizationService userAuthorizationService;

  @Mock
  private SpotifyArtistImportService artistImportService;

  @InjectMocks
  private SpotifyRestController underTest;

  private RestAssuredMockMvcUtils authorizationRestAssuredMockMvcUtils;
  private RestAssuredMockMvcUtils importRestAssuredMockMvcUtils;

  @BeforeEach
  void setup() {
    authorizationRestAssuredMockMvcUtils = new RestAssuredMockMvcUtils(Endpoints.Rest.SPOTIFY_AUTHORIZATION);
    importRestAssuredMockMvcUtils = new RestAssuredMockMvcUtils(Endpoints.Rest.SPOTIFY_ARTIST_IMPORT);
    RestAssuredMockMvc.standaloneSetup(underTest,
                                       springSecurity((request, response, chain) -> chain.doFilter(request, response)));
  }

  @AfterEach
  void tearDown() {
    reset(userAuthorizationService, artistImportService);
  }

  @Test
  @DisplayName("GET on " + Endpoints.Rest.SPOTIFY_AUTHORIZATION + " should return 200")
  void test_get_authorization_returns_ok() {
    // when
    var validatableResponse = authorizationRestAssuredMockMvcUtils.doGet();

    // then
    validatableResponse.statusCode(HttpStatus.OK.value());
  }

  @Test
  @DisplayName("GET on " + Endpoints.Rest.SPOTIFY_AUTHORIZATION + " should call SpotifyUserAuthorizationServiceService")
  void test_get_authorization_calls_spotify_service() {
    // when
    authorizationRestAssuredMockMvcUtils.doGet();

    // then
    verify(userAuthorizationService).prepareAuthorization();
  }

  @Test
  @DisplayName("GET on " + Endpoints.Rest.SPOTIFY_AUTHORIZATION + " should return expected url")
  void test_get_authorization_returns_url() {
    // given
    var expectedUrl = "i'm an url";
    doReturn(expectedUrl).when(userAuthorizationService).prepareAuthorization();

    // when
    var validatableResponse = authorizationRestAssuredMockMvcUtils.doGet();

    // then
    var response = validatableResponse.extract().as(SpotifyUserAuthorizationResponse.class);
    assertThat(response.getAuthorizationUrl()).isEqualTo(expectedUrl);
  }

  @Test
  @DisplayName("GET on " + Endpoints.Rest.SPOTIFY_ARTIST_IMPORT + " should return 200")
  void test_get_import_returns_ok() {
    // when
    var validatableResponse = importRestAssuredMockMvcUtils.doPost();

    // then
    validatableResponse.statusCode(HttpStatus.OK.value());
  }

  @Test
  @DisplayName("GET on " + Endpoints.Rest.SPOTIFY_ARTIST_IMPORT + " should call SpotifyArtistImportService")
  void test_get_import_calls_spotify_service() {
    // when
    importRestAssuredMockMvcUtils.doPost();

    // then
    verify(artistImportService).importArtistsFromLikedReleases();
  }

  @Test
  @DisplayName("GET on " + Endpoints.Rest.SPOTIFY_ARTIST_IMPORT + " return the import result")
  void test_get_import_returns_result() {
    // given
    var expectedResult = List.of(ArtistDtoFactory.createDefault());
    doReturn(expectedResult).when(artistImportService).importArtistsFromLikedReleases();

    // when
    var validatableResponse = importRestAssuredMockMvcUtils.doPost();

    // then
    var response = validatableResponse.extract().as(SpotifyArtistImportResponse.class);
    assertThat(response.getArtists()).isEqualTo(expectedResult);
  }
}
