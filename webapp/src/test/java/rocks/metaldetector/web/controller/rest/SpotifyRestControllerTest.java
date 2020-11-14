package rocks.metaldetector.web.controller.rest;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rocks.metaldetector.service.spotify.SpotifyFollowedArtistsService;
import rocks.metaldetector.service.spotify.SpotifyUserAuthorizationService;
import rocks.metaldetector.support.Endpoints;
import rocks.metaldetector.testutil.DtoFactory.ArtistDtoFactory;
import rocks.metaldetector.web.RestAssuredMockMvcUtils;
import rocks.metaldetector.web.api.request.SpotifyAuthorizationRequest;
import rocks.metaldetector.web.api.response.SpotifyArtistImportResponse;
import rocks.metaldetector.web.api.response.SpotifyFollowedArtistsResponse;
import rocks.metaldetector.web.api.response.SpotifyUserAuthorizationResponse;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;
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

  @BeforeEach
  void setup() {
    RestAssuredMockMvc.standaloneSetup(underTest,
                                       springSecurity((request, response, chain) -> chain.doFilter(request, response)));
  }

  @AfterEach
  void tearDown() {
    reset(userAuthorizationService, artistImportService);
  }

  @Nested
  @DisplayName("Tests for prepare authorization endpoint")
  class PrepareAuthorizationTest {

    private RestAssuredMockMvcUtils restAssuredMockMvcUtils;

    @BeforeEach
    void setup() {
      restAssuredMockMvcUtils = new RestAssuredMockMvcUtils(Endpoints.Rest.SPOTIFY_AUTHORIZATION);
    }

    @Test
    @DisplayName("POST on " + Endpoints.Rest.SPOTIFY_AUTHORIZATION + " should return 200")
    void test_post_authorization_returns_ok() {
      // when
      var validatableResponse = restAssuredMockMvcUtils.doPost();

      // then
      validatableResponse.statusCode(OK.value());
    }

    @Test
    @DisplayName("POST on " + Endpoints.Rest.SPOTIFY_AUTHORIZATION + " should call SpotifyUserAuthorizationServiceService")
    void test_post_authorization_calls_spotify_service() {
      // when
      restAssuredMockMvcUtils.doPost();

      // then
      verify(userAuthorizationService).prepareAuthorization();
    }

    @Test
    @DisplayName("POST on " + Endpoints.Rest.SPOTIFY_AUTHORIZATION + " should return expected url")
    void test_post_authorization_returns_url() {
      // given
      var expectedUrl = "i'm an url";
      doReturn(expectedUrl).when(userAuthorizationService).prepareAuthorization();

      // when
      var validatableResponse = restAssuredMockMvcUtils.doPost();

      // then
      var response = validatableResponse.extract().as(SpotifyUserAuthorizationResponse.class);
      assertThat(response.getAuthorizationUrl()).isEqualTo(expectedUrl);
    }
  }

  @Nested
  @DisplayName("Tests for import endpoint")
  class ImportTest {

    private RestAssuredMockMvcUtils restAssuredMockMvcUtils;

    @BeforeEach
    void setup() {
      restAssuredMockMvcUtils = new RestAssuredMockMvcUtils(Endpoints.Rest.SPOTIFY_ARTIST_IMPORT);
    }

    @Test
    @DisplayName("POST on " + Endpoints.Rest.SPOTIFY_ARTIST_IMPORT + " should return 200")
    void test_post_import_returns_ok() {
      // when
      var validatableResponse = restAssuredMockMvcUtils.doPost();

      // then
      validatableResponse.statusCode(OK.value());
    }

    @Test
    @DisplayName("POST on " + Endpoints.Rest.SPOTIFY_ARTIST_IMPORT + " should call SpotifyFollowedArtistsService")
    void test_post_import_calls_spotify_service() {
      // when
      restAssuredMockMvcUtils.doPost();

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
      var validatableResponse = restAssuredMockMvcUtils.doPost();

      // then
      var response = validatableResponse.extract().as(SpotifyArtistImportResponse.class);
      assertThat(response.getArtists()).isEqualTo(expectedResult);
    }
  }

  @Nested
  @DisplayName("Tests for getting followed artists")
  class GetFollowedTest {

    private RestAssuredMockMvcUtils restAssuredMockMvcUtils;

    @BeforeEach
    void setup() {
      restAssuredMockMvcUtils = new RestAssuredMockMvcUtils(Endpoints.Rest.SPOTIFY_FOLLOWED_ARTISTS);
      RestAssuredMockMvc.standaloneSetup(underTest,
                                         springSecurity((request, response, chain) -> chain.doFilter(request, response)));
    }

    @Test
    @DisplayName("GET on " + Endpoints.Rest.SPOTIFY_FOLLOWED_ARTISTS + " should return 200 for valid fetch type")
    void test_get_followed_returns_ok() {
      // when
      var validatableResponse = restAssuredMockMvcUtils.doGet(Map.of(FETCH_TYPES_PARAM, List.of(ALBUMS)));

      // then
      validatableResponse.statusCode(OK.value());
    }

    @Test
    @DisplayName("GET on " + Endpoints.Rest.SPOTIFY_FOLLOWED_ARTISTS + " should return 400 for invalid fetch type")
    void test_get_followed_returns_bad_request() {
      // when
      var validatableResponse = restAssuredMockMvcUtils.doGet(Map.of(FETCH_TYPES_PARAM, List.of("ARTISTS")));

      // then
      validatableResponse.statusCode(BAD_REQUEST.value());
    }

    @Test
    @DisplayName("GET on " + Endpoints.Rest.SPOTIFY_ARTIST_IMPORT + " should call SpotifyFollowedArtistsService")
    void test_get_followed_calls_spotify_service() {
      // given
      var fetchTypes = List.of(ALBUMS);

      // when
      restAssuredMockMvcUtils.doGet(Map.of(FETCH_TYPES_PARAM, fetchTypes));

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
      var validatableResponse = restAssuredMockMvcUtils.doGet(Map.of(FETCH_TYPES_PARAM, List.of(ALBUMS)));

      // then
      var response = validatableResponse.extract().as(SpotifyFollowedArtistsResponse.class);
      assertThat(response.getArtists()).isEqualTo(expectedResult);
    }
  }

  @Nested
  @TestInstance(TestInstance.Lifecycle.PER_CLASS)
  @DisplayName("Tests for callback endpoint")
  class CallbackTest {

    private RestAssuredMockMvcUtils restAssuredMockMvcUtils;

    @BeforeEach
    void setup() {
      restAssuredMockMvcUtils = new RestAssuredMockMvcUtils(Endpoints.Rest.SPOTIFY_AUTHORIZATION_PERSIST);
    }

    @Test
    @DisplayName("POST on " + Endpoints.Rest.SPOTIFY_AUTHORIZATION_PERSIST + " should return 200")
    void test_post_authorization_returns_ok() {
      // given
      var request = SpotifyAuthorizationRequest.builder().code("code").state("state").build();

      // when
      var validatableResponse = restAssuredMockMvcUtils.doPost(request);

      // then
      validatableResponse.statusCode(OK.value());
    }

    @Test
    @DisplayName("POST on " + Endpoints.Rest.SPOTIFY_AUTHORIZATION_PERSIST + " should call SpotifyUserAuthorizationServiceService")
    void test_post_authorization_calls_spotify_service() {
      // given
      var request = SpotifyAuthorizationRequest.builder().code("code").state("state").build();

      // when
      restAssuredMockMvcUtils.doPost(request);

      // then
      verify(userAuthorizationService).persistInitialToken(request.getState(), request.getCode());
    }

    @ParameterizedTest
    @MethodSource("badRequestProvider")
    @DisplayName("POST on " + Endpoints.Rest.SPOTIFY_AUTHORIZATION_PERSIST + " should return bad request")
    void test_post_authorization_returns_url(String code, String state) {
      // given
      var request = SpotifyAuthorizationRequest.builder().code(code).state(state).build();

      // when
      var validatableResponse = restAssuredMockMvcUtils.doPost(request);

      // then
      validatableResponse.statusCode(BAD_REQUEST.value());
    }

    private Stream<Arguments> badRequestProvider() {
      return Stream.of(
          Arguments.of((Object) null, (Object) null),
          Arguments.of("", "")
      );
    }
  }
}
