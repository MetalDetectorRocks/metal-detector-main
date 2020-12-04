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
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import rocks.metaldetector.persistence.domain.artist.ArtistSource;
import rocks.metaldetector.service.artist.ArtistSearchService;
import rocks.metaldetector.service.artist.ArtistService;
import rocks.metaldetector.service.artist.FollowArtistService;
import rocks.metaldetector.service.exceptions.RestExceptionsHandler;
import rocks.metaldetector.support.Endpoints;
import rocks.metaldetector.web.RestAssuredMockMvcUtils;
import rocks.metaldetector.web.api.response.ArtistSearchResponse;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static rocks.metaldetector.persistence.domain.artist.ArtistSource.DISCOGS;
import static rocks.metaldetector.testutil.DtoFactory.ArtistSearchResponseFactory;
import static rocks.metaldetector.web.controller.rest.ArtistsRestController.DEFAULT_DISCOGS_PAGE;
import static rocks.metaldetector.web.controller.rest.ArtistsRestController.DEFAULT_DISCOGS_SIZE;

@ExtendWith(MockitoExtension.class)
class ArtistsRestControllerTest implements WithAssertions {

  private static final String VALID_EXTERNAL_ID = "252211";
  private static final String VALID_SOURCE_STRING = "Discogs";
  private static final String VALID_SEARCH_REQUEST = "Darkthrone";
  private static final ArtistSource ARTIST_SOURCE = DISCOGS;

  @Mock
  private ArtistService artistService;

  @Mock
  private ArtistSearchService artistSearchService;

  @Mock
  private FollowArtistService followArtistService;

  private ArtistsRestController underTest;

  private RestAssuredMockMvcUtils restAssuredUtils;

  @BeforeEach
  void setup() {
    underTest = new ArtistsRestController(artistSearchService, followArtistService);
  }

  @Nested
  @TestInstance(TestInstance.Lifecycle.PER_CLASS)
  @DisplayName("Test artist name search endpoint")
  class ArtistNameSearchTest {

    private static final int DEFAULT_PAGE = 1;
    private static final int DEFAULT_SIZE = 10;

    @BeforeEach
    void setUp() {
      restAssuredUtils = new RestAssuredMockMvcUtils(Endpoints.Rest.ARTISTS + Endpoints.Rest.SEARCH);
      RestAssuredMockMvc.standaloneSetup(underTest, RestExceptionsHandler.class);
    }

    @AfterEach
    void tearDown() {
      reset(artistService, artistSearchService, followArtistService);
    }

    @Test
    @DisplayName("Should pass request parameter to spotify search")
    void handleNameSearch_default_spotify() {
      // given
      Map<String, Object> requestParams = new HashMap<>();
      requestParams.put("query", VALID_SEARCH_REQUEST);
      requestParams.put("page", DEFAULT_PAGE);
      requestParams.put("size", DEFAULT_SIZE);

      // when
      restAssuredUtils.doGet(requestParams);

      // then
      verify(artistSearchService).searchSpotifyByName(VALID_SEARCH_REQUEST, PageRequest.of(DEFAULT_PAGE, DEFAULT_SIZE));
    }

    @Test
    @DisplayName("Should pass default parameter to discogs search if spotify does not return results for first page")
    void handleNameSearch_fallback_discogs() {
      // given
      Map<String, Object> requestParams = new HashMap<>();
      requestParams.put("query", VALID_SEARCH_REQUEST);
      requestParams.put("page", 1);
      requestParams.put("size", DEFAULT_SIZE);
      doReturn(ArtistSearchResponseFactory.empty()).when(artistSearchService).searchSpotifyByName(any(), any());

      // when
      restAssuredUtils.doGet(requestParams);

      // then
      verify(artistSearchService).searchDiscogsByName(VALID_SEARCH_REQUEST, PageRequest.of(DEFAULT_DISCOGS_PAGE, DEFAULT_DISCOGS_SIZE));
    }

    @Test
    @DisplayName("Should not pass default parameter to discogs search if spotify does not return results for all other pages than first page")
    void handleNameSearch_no_fallback_to_discogs() {
      // given
      Map<String, Object> requestParams = new HashMap<>();
      requestParams.put("query", VALID_SEARCH_REQUEST);
      requestParams.put("page", 2);
      requestParams.put("size", DEFAULT_SIZE);
      doReturn(ArtistSearchResponseFactory.empty()).when(artistSearchService).searchSpotifyByName(any(), any());

      // when
      restAssuredUtils.doGet(requestParams);

      // then
      verify(artistSearchService, times(0)).searchDiscogsByName(VALID_SEARCH_REQUEST, PageRequest.of(DEFAULT_DISCOGS_PAGE, DEFAULT_DISCOGS_SIZE));
    }

    @Test
    @DisplayName("Should return status code 200")
    void handleNameSearch_return_200() {
      // given
      Map<String, Object> requestParams = Map.of(
          "query", VALID_SEARCH_REQUEST,
          "page", DEFAULT_PAGE,
          "size", DEFAULT_SIZE
      );
      doReturn(ArtistSearchResponseFactory.spotify()).when(artistSearchService).searchSpotifyByName(any(), any());

      // when
      var validatableResponse = restAssuredUtils.doGet(requestParams);

      // then
      validatableResponse
          .statusCode(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("Should return response transformer's result for spotify")
    void handleNameSearch_return_result_spotify() {
      // given
      var expectedSearchResult = ArtistSearchResponseFactory.spotify();
      Map<String, Object> requestParams = Map.of(
          "query", VALID_SEARCH_REQUEST,
          "page", DEFAULT_PAGE,
          "size", DEFAULT_SIZE
      );
      doReturn(expectedSearchResult).when(artistSearchService).searchSpotifyByName(any(), any());

      // when
      var validatableResponse = restAssuredUtils.doGet(requestParams);

      // then
      ArtistSearchResponse searchResponse = validatableResponse.extract().as(ArtistSearchResponse.class);
      assertThat(searchResponse).isEqualTo(expectedSearchResult);
    }
  }

  @Nested
  @TestInstance(TestInstance.Lifecycle.PER_CLASS)
  @DisplayName("Test follow/unfollow endpoints")
  class FollowArtistTest {

    private RestAssuredMockMvcUtils followArtistRestAssuredUtils;
    private RestAssuredMockMvcUtils unfollowArtistRestAssuredUtils;

    @BeforeEach
    void setUp() {
      followArtistRestAssuredUtils = new RestAssuredMockMvcUtils(Endpoints.Rest.ARTISTS + Endpoints.Rest.FOLLOW);
      unfollowArtistRestAssuredUtils = new RestAssuredMockMvcUtils(Endpoints.Rest.ARTISTS + Endpoints.Rest.UNFOLLOW);
      RestAssuredMockMvc.standaloneSetup(underTest, RestExceptionsHandler.class);
    }

    @AfterEach
    void tearDown() {
      reset(artistService, followArtistService);
    }

    @Test
    @DisplayName("Should return 200 when following an artist")
    void handle_follow_return_200() {
      // given
      var url = "/" + VALID_SOURCE_STRING + "/" + VALID_EXTERNAL_ID;

      // when
      var validatableResponse = followArtistRestAssuredUtils.doPost(url);

      // then
      validatableResponse.statusCode(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("Should call follow artist service when following an artist")
    void handle_follow_call_follow_artist_service() {
      // given
      var url = "/" + VALID_SOURCE_STRING + "/" + VALID_EXTERNAL_ID;

      // when
      followArtistRestAssuredUtils.doPost(url);

      // then
      verify(followArtistService).follow(VALID_EXTERNAL_ID, ARTIST_SOURCE);
    }

    @Test
    @DisplayName("Should return bad request on invalid source")
    void handle_follow_bad_request() {
      // given
      var INVALID_SOURCE = "something-stupid";
      var url = "/" + INVALID_SOURCE + "/" + VALID_EXTERNAL_ID;

      // when
      var result = followArtistRestAssuredUtils.doPost(url);

      // then
      result.status(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("Should return 200 when unfollowing an artist")
    void handle_unfollow_return_200() {
      // given
      var url = "/" + VALID_SOURCE_STRING + "/" + VALID_EXTERNAL_ID;

      // when
      var validatableResponse = unfollowArtistRestAssuredUtils.doPost(url);

      // then
      validatableResponse.statusCode(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("Should call follow artist service when unfollowing an artist")
    void handle_unfollow_call_follow_artist_service() {
      // given
      var url = "/" + VALID_SOURCE_STRING + "/" + VALID_EXTERNAL_ID;

      // when
      unfollowArtistRestAssuredUtils.doPost(url);

      // then
      verify(followArtistService).unfollow(VALID_EXTERNAL_ID, ARTIST_SOURCE);
    }

    @Test
    @DisplayName("Should return bad request on invalid source")
    void handle_unfollow_bad_request() {
      // given
      var INVALID_SOURCE = "something-stupid";
      var url = "/" + INVALID_SOURCE + "/" + VALID_EXTERNAL_ID;

      // when
      var result = unfollowArtistRestAssuredUtils.doPost(url);

      // then
      result.status(HttpStatus.BAD_REQUEST);
    }
  }
}
