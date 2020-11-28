package rocks.metaldetector.web.controller.rest;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rocks.metaldetector.service.spotify.SpotifyFollowedArtistsService;
import rocks.metaldetector.support.Endpoints;
import rocks.metaldetector.testutil.DtoFactory.ArtistDtoFactory;
import rocks.metaldetector.web.RestAssuredMockMvcUtils;
import rocks.metaldetector.web.api.response.SpotifyArtistImportResponse;
import rocks.metaldetector.web.api.response.SpotifyFollowedArtistsResponse;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;
import static rocks.metaldetector.service.spotify.SpotifyFetchType.ALBUMS;
import static rocks.metaldetector.testutil.DtoFactory.SpotifyArtistDtoFactory;
import static rocks.metaldetector.web.controller.rest.SpotifySynchronizationRestController.FETCH_TYPES_PARAM;

@ExtendWith(MockitoExtension.class)
class SpotifySynchronizationRestControllerTest implements WithAssertions {

  @Mock
  private SpotifyFollowedArtistsService artistImportService;

  @InjectMocks
  private SpotifySynchronizationRestController underTest;

  @BeforeEach
  void setup() {
    RestAssuredMockMvc.standaloneSetup(underTest);
  }

  @AfterEach
  void tearDown() {
    reset(artistImportService);
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
      RestAssuredMockMvc.standaloneSetup(underTest);
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
      var expectedResult = List.of(SpotifyArtistDtoFactory.createDefault());
      doReturn(expectedResult).when(artistImportService).getNewFollowedArtists(any());

      // when
      var validatableResponse = restAssuredMockMvcUtils.doGet(Map.of(FETCH_TYPES_PARAM, List.of(ALBUMS)));

      // then
      var response = validatableResponse.extract().as(SpotifyFollowedArtistsResponse.class);
      assertThat(response.getArtists()).isEqualTo(expectedResult);
    }
  }
}
