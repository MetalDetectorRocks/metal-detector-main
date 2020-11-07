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
import rocks.metaldetector.service.spotify.SpotifyFollowedArtistsService;
import rocks.metaldetector.service.spotify.SpotifyUserAuthorizationService;
import rocks.metaldetector.support.Endpoints;
import rocks.metaldetector.testutil.DtoFactory.ArtistDtoFactory;
import rocks.metaldetector.web.RestAssuredMockMvcUtils;
import rocks.metaldetector.web.api.response.SpotifyArtistImportResponse;
import rocks.metaldetector.web.api.response.SpotifyFollowedArtistsResponse;
import rocks.metaldetector.web.api.response.SpotifyUserAuthorizationResponse;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static rocks.metaldetector.service.spotify.SpotifyFetchType.ALBUMS;
import static rocks.metaldetector.web.controller.rest.SpotifyRestController.FETCH_TYPES_PARAM;

@ExtendWith(MockitoExtension.class)
class SpotifyRestControllerTest implements WithAssertions {

  @Mock
  private SpotifyUserAuthorizationService userAuthorizationService;

  @Mock
  private SpotifyFollowedArtistsService artistImportService;

  @InjectMocks
  private SpotifyRestController underTest;

  private RestAssuredMockMvcUtils authorizationRestAssuredMockMvcUtils;
  private RestAssuredMockMvcUtils importRestAssuredMockMvcUtils;
  private RestAssuredMockMvcUtils followedRestAssuredMockMvcUtils;

  @BeforeEach
  void setup() {
    authorizationRestAssuredMockMvcUtils = new RestAssuredMockMvcUtils(Endpoints.Rest.SPOTIFY_AUTHORIZATION);
    importRestAssuredMockMvcUtils = new RestAssuredMockMvcUtils(Endpoints.Rest.SPOTIFY_ARTIST_IMPORT);
    followedRestAssuredMockMvcUtils = new RestAssuredMockMvcUtils(Endpoints.Rest.SPOTIFY_FOLLOWED_ARTISTS);
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
  @DisplayName("POST on " + Endpoints.Rest.SPOTIFY_ARTIST_IMPORT + " should return 200")
  void test_post_import_returns_ok() {
    // when
    var validatableResponse = importRestAssuredMockMvcUtils.doPost();

    // then
    validatableResponse.statusCode(HttpStatus.OK.value());
  }

  @Test
  @DisplayName("POST on " + Endpoints.Rest.SPOTIFY_ARTIST_IMPORT + " should call SpotifyFollowedArtistsService")
  void test_post_import_calls_spotify_service() {
    // when
    importRestAssuredMockMvcUtils.doPost();

    // then
    verify(artistImportService).importArtistsFromLikedReleases();
  }

  @Test
  @DisplayName("POST on " + Endpoints.Rest.SPOTIFY_ARTIST_IMPORT + " return the import result")
  void test_post_import_returns_result() {
    // given
    var expectedResult = List.of(ArtistDtoFactory.createDefault());
    doReturn(expectedResult).when(artistImportService).importArtistsFromLikedReleases();

    // when
    var validatableResponse = importRestAssuredMockMvcUtils.doPost();

    // then
    var response = validatableResponse.extract().as(SpotifyArtistImportResponse.class);
    assertThat(response.getArtists()).isEqualTo(expectedResult);
  }

  @Test
  @DisplayName("GET on " + Endpoints.Rest.SPOTIFY_FOLLOWED_ARTISTS + " should return 200")
  void test_get_followed_returns_ok() {
    // when
    var validatableResponse = followedRestAssuredMockMvcUtils.doGet(Map.of(FETCH_TYPES_PARAM, Collections.emptyList()));

    // then
    validatableResponse.statusCode(HttpStatus.OK.value());
  }

  @Test
  @DisplayName("GET on " + Endpoints.Rest.SPOTIFY_FOLLOWED_ARTISTS + " should return 400 for invalid fetch type")
  void test_get_followed_returns_bad_request() {
    // when
    var validatableResponse = followedRestAssuredMockMvcUtils.doGet(Map.of(FETCH_TYPES_PARAM, List.of("ARTISTS")));

    // then
    validatableResponse.statusCode(HttpStatus.BAD_REQUEST.value());
  }

  @Test
  @DisplayName("GET on " + Endpoints.Rest.SPOTIFY_ARTIST_IMPORT + " should call SpotifyFollowedArtistsService")
  void test_get_followed_calls_spotify_service() {
    // given
    var fetchTypes = List.of(ALBUMS);

    // when
    followedRestAssuredMockMvcUtils.doGet(Map.of(FETCH_TYPES_PARAM, fetchTypes));

    // then
    verify(artistImportService).getNewFollowedArtists(fetchTypes);
  }

  @Test
  @DisplayName("GET on " + Endpoints.Rest.SPOTIFY_FOLLOWED_ARTISTS + " return the followed artists")
  void test_get_followed_returns_result() {
    // given
    var expectedResult = List.of(ArtistDtoFactory.createDefault());
    doReturn(expectedResult).when(artistImportService).getNewFollowedArtists(any());

    // when
    var validatableResponse = followedRestAssuredMockMvcUtils.doGet(Map.of(FETCH_TYPES_PARAM, Collections.emptyList()));

    // then
    var response = validatableResponse.extract().as(SpotifyFollowedArtistsResponse.class);
    assertThat(response.getArtists()).isEqualTo(expectedResult);
  }
}
