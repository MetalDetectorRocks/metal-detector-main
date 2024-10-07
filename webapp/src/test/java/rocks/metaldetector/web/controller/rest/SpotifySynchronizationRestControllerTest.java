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
import rocks.metaldetector.service.spotify.SpotifyFetchType;
import rocks.metaldetector.service.spotify.SpotifySynchronizationService;
import rocks.metaldetector.support.Endpoints;
import rocks.metaldetector.testutil.DtoFactory;
import rocks.metaldetector.web.RestAssuredMockMvcUtils;
import rocks.metaldetector.web.api.response.SpotifyArtistSynchronizationResponse;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpStatus.OK;

@ExtendWith(MockitoExtension.class)
class SpotifySynchronizationRestControllerTest implements WithAssertions {

  @Mock
  private SpotifySynchronizationService spotifySynchronizationService;

  @InjectMocks
  private SpotifySynchronizationRestController underTest;

  private RestAssuredMockMvcUtils restAssuredMockMvcUtils;

  @BeforeEach
  void setup() {
    RestAssuredMockMvc.standaloneSetup(underTest);
    restAssuredMockMvcUtils = new RestAssuredMockMvcUtils(Endpoints.Rest.SPOTIFY_ARTIST_SYNCHRONIZATION);
  }

  @AfterEach
  void tearDown() {
    reset(spotifySynchronizationService);
  }

  @Test
  @DisplayName("POST on " + Endpoints.Rest.SPOTIFY_ARTIST_SYNCHRONIZATION + " should return 200")
  void test_returns_ok() {
    // when
    var validatableResponse = restAssuredMockMvcUtils.doPost();

    // then
    validatableResponse.statusCode(OK.value());
  }

  @Test
  @DisplayName("POST on " + Endpoints.Rest.SPOTIFY_ARTIST_SYNCHRONIZATION + " calls SpotifySynchronizationService to fetch artists")
  void test_calls_spotify_service_fetch() {
    // given
    var expectedFetchTypes = Arrays.stream(SpotifyFetchType.values()).toList();

    // when
    restAssuredMockMvcUtils.doPost();

    // then
    verify(spotifySynchronizationService).fetchSavedArtists(expectedFetchTypes);
  }

  @Test
  @DisplayName("POST on " + Endpoints.Rest.SPOTIFY_ARTIST_SYNCHRONIZATION + " calls SpotifySynchronizationService to fetch artists")
  void test_calls_spotify_service_sync() {
    // given
    var spotifyArtists = List.of(DtoFactory.SpotifyArtistDtoFactory.withArtistId("abc"),
        DtoFactory.SpotifyArtistDtoFactory.withArtistId("def"));
    doReturn(spotifyArtists).when(spotifySynchronizationService).fetchSavedArtists(any());

    // when
    restAssuredMockMvcUtils.doPost();

    // then
    verify(spotifySynchronizationService).synchronizeArtists(List.of("abc", "def"));
  }

  @Test
  @DisplayName("POST on " + Endpoints.Rest.SPOTIFY_ARTIST_SYNCHRONIZATION + " return the artist names")
  void test_returns_result() {
    // given
    var artistNames = List.of("abc", "def");
    doReturn(artistNames).when(spotifySynchronizationService).synchronizeArtists(any());

    // when
    var validatableResponse = restAssuredMockMvcUtils.doPost();

    // then
    var response = validatableResponse.extract().as(SpotifyArtistSynchronizationResponse.class);
    assertThat(response.getArtistNames()).isEqualTo(artistNames);
  }
}
