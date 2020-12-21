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
import rocks.metaldetector.service.spotify.SpotifySynchronizationService;
import rocks.metaldetector.support.Endpoints;
import rocks.metaldetector.web.RestAssuredMockMvcUtils;
import rocks.metaldetector.web.api.request.SynchronizeArtistsRequest;
import rocks.metaldetector.web.api.response.SpotifyArtistSynchronizationResponse;
import rocks.metaldetector.web.api.response.SpotifyFetchArtistsResponse;

import java.util.Collections;
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
  private SpotifySynchronizationService spotifySynchronizationService;

  @InjectMocks
  private SpotifySynchronizationRestController underTest;

  @BeforeEach
  void setup() {
    RestAssuredMockMvc.standaloneSetup(underTest);
  }

  @AfterEach
  void tearDown() {
    reset(spotifySynchronizationService);
  }

  @Nested
  @DisplayName("Tests for synchronization endpoint")
  class SynchronizationTest {

    private RestAssuredMockMvcUtils restAssuredMockMvcUtils;

    @BeforeEach
    void setup() {
      restAssuredMockMvcUtils = new RestAssuredMockMvcUtils(Endpoints.Rest.SPOTIFY_ARTIST_SYNCHRONIZATION);
    }

    @Test
    @DisplayName("POST on " + Endpoints.Rest.SPOTIFY_ARTIST_SYNCHRONIZATION + " should return 200")
    void test_returns_ok() {
      // given
      var request = SynchronizeArtistsRequest.builder().artistIds(Collections.emptyList()).build();

      // when
      var validatableResponse = restAssuredMockMvcUtils.doPost(request);

      // then
      validatableResponse.statusCode(OK.value());
    }

    @Test
    @DisplayName("POST on " + Endpoints.Rest.SPOTIFY_ARTIST_SYNCHRONIZATION + " should return 400 for invalid request")
    void test_returns_bad_request() {
      // when
      var request = new SynchronizeArtistsRequest();

      // when
      var validatableResponse = restAssuredMockMvcUtils.doPost(request);

      // then
      validatableResponse.statusCode(BAD_REQUEST.value());
    }

    @Test
    @DisplayName("POST on " + Endpoints.Rest.SPOTIFY_ARTIST_SYNCHRONIZATION + " calls SpotifySynchronizationService")
    void test_calls_spotify_service() {
      // given
      var request = SynchronizeArtistsRequest.builder().artistIds(Collections.emptyList()).build();

      // when
      restAssuredMockMvcUtils.doPost(request);

      // then
      verify(spotifySynchronizationService).synchronizeArtists(request.getArtistIds());
    }

    @Test
    @DisplayName("POST on " + Endpoints.Rest.SPOTIFY_ARTIST_SYNCHRONIZATION + " return the import result")
    void test_returns_result() {
      // given
      var artistCount = 666;
      doReturn(artistCount).when(spotifySynchronizationService).synchronizeArtists(any());
      var request = SynchronizeArtistsRequest.builder().artistIds(Collections.emptyList()).build();

      // when
      var validatableResponse = restAssuredMockMvcUtils.doPost(request);

      // then
      var response = validatableResponse.extract().as(SpotifyArtistSynchronizationResponse.class);
      assertThat(response.getArtistsCount()).isEqualTo(artistCount);
    }
  }

  @Nested
  @DisplayName("Tests for getting saved artists from spotify")
  class FetchSavedSpotifyArtistsTest {

    private RestAssuredMockMvcUtils restAssuredMockMvcUtils;

    @BeforeEach
    void setup() {
      restAssuredMockMvcUtils = new RestAssuredMockMvcUtils(Endpoints.Rest.SPOTIFY_SAVED_ARTISTS);
    }

    @Test
    @DisplayName("GET on " + Endpoints.Rest.SPOTIFY_SAVED_ARTISTS + " should return 200 for valid fetch type")
    void test_returns_ok() {
      // when
      var validatableResponse = restAssuredMockMvcUtils.doGet(Map.of(FETCH_TYPES_PARAM, List.of(ALBUMS)));

      // then
      validatableResponse.statusCode(OK.value());
    }

    @Test
    @DisplayName("GET on " + Endpoints.Rest.SPOTIFY_SAVED_ARTISTS + " should return 400 for invalid fetch type")
    void test_returns_bad_request() {
      // when
      var validatableResponse = restAssuredMockMvcUtils.doGet(Map.of(FETCH_TYPES_PARAM, List.of("UNKNOWN")));

      // then
      validatableResponse.statusCode(BAD_REQUEST.value());
    }

    @Test
    @DisplayName("GET on " + Endpoints.Rest.SPOTIFY_SAVED_ARTISTS + " should call SpotifyFollowedArtistsService")
    void test_calls_spotify_service() {
      // given
      var fetchTypes = List.of(ALBUMS);

      // when
      restAssuredMockMvcUtils.doGet(Map.of(FETCH_TYPES_PARAM, fetchTypes));

      // then
      verify(spotifySynchronizationService).fetchSavedArtists(fetchTypes);
    }

    @Test
    @DisplayName("GET on " + Endpoints.Rest.SPOTIFY_SAVED_ARTISTS + " return the followed artists")
    void test_returns_result() {
      // given
      var expectedResult = List.of(SpotifyArtistDtoFactory.createDefault());
      doReturn(expectedResult).when(spotifySynchronizationService).fetchSavedArtists(any());

      // when
      var validatableResponse = restAssuredMockMvcUtils.doGet(Map.of(FETCH_TYPES_PARAM, List.of(ALBUMS)));

      // then
      var response = validatableResponse.extract().as(SpotifyFetchArtistsResponse.class);
      assertThat(response.getArtists()).isEqualTo(expectedResult);
    }
  }
}
