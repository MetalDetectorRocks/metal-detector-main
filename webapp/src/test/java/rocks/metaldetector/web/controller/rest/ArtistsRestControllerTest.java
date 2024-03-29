package rocks.metaldetector.web.controller.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
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
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import rocks.metaldetector.persistence.domain.artist.ArtistSource;
import rocks.metaldetector.service.artist.ArtistDto;
import rocks.metaldetector.service.artist.ArtistSearchService;
import rocks.metaldetector.service.artist.FollowArtistService;
import rocks.metaldetector.service.dashboard.ArtistCollector;
import rocks.metaldetector.service.exceptions.RestExceptionsHandler;
import rocks.metaldetector.testutil.DtoFactory.ArtistDtoFactory;
import rocks.metaldetector.web.RestAssuredMockMvcUtils;
import rocks.metaldetector.web.api.response.ArtistSearchResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static rocks.metaldetector.persistence.domain.artist.ArtistSource.DISCOGS;
import static rocks.metaldetector.support.Endpoints.Rest.FOLLOW_ARTIST;
import static rocks.metaldetector.support.Endpoints.Rest.SEARCH_ARTIST;
import static rocks.metaldetector.support.Endpoints.Rest.TOP_ARTISTS;
import static rocks.metaldetector.support.Endpoints.Rest.UNFOLLOW_ARTIST;
import static rocks.metaldetector.testutil.DtoFactory.ArtistSearchResponseFactory;
import static rocks.metaldetector.web.controller.rest.ArtistsRestController.DEFAULT_DISCOGS_PAGE;
import static rocks.metaldetector.web.controller.rest.ArtistsRestController.DEFAULT_DISCOGS_SIZE;

@ExtendWith(MockitoExtension.class)
class ArtistsRestControllerTest implements WithAssertions {

  private static final String VALID_EXTERNAL_ID = "252211";
  private static final String VALID_SOURCE_STRING = DISCOGS.name();
  private static final String VALID_SEARCH_REQUEST = "Darkthrone";
  private static final ArtistSource ARTIST_SOURCE = DISCOGS;

  @Mock
  private ArtistSearchService artistSearchService;

  @Mock
  private FollowArtistService followArtistService;

  @Mock
  private ArtistCollector artistCollector;

  private ArtistsRestController underTest;

  private RestAssuredMockMvcUtils restAssuredUtils;

  @BeforeEach
  void setup() {
    underTest = new ArtistsRestController(artistSearchService, followArtistService, artistCollector);
  }

  @Nested
  @TestInstance(PER_CLASS)
  @DisplayName("Test artist name search endpoint")
  class ArtistNameSearchTest {

    private static final int DEFAULT_PAGE = 1;
    private static final int DEFAULT_SIZE = 10;

    @BeforeEach
    void setUp() {
      restAssuredUtils = new RestAssuredMockMvcUtils(SEARCH_ARTIST);
      RestAssuredMockMvc.standaloneSetup(underTest, RestExceptionsHandler.class);
    }

    @AfterEach
    void tearDown() {
      reset(artistSearchService, followArtistService);
    }

    @Test
    @DisplayName("Should pass request parameter to spotify search")
    void handleNameSearch_default_spotify() {
      // given
      Map<String, Object> requestParams = new HashMap<>();
      requestParams.put("query", VALID_SEARCH_REQUEST);
      requestParams.put("page", DEFAULT_PAGE);
      requestParams.put("size", DEFAULT_SIZE);
      doReturn(ArtistSearchResponseFactory.empty()).when(artistSearchService).searchSpotifyByName(any(), any());

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
          .statusCode(OK.value());
    }

    @Test
    @DisplayName("Should return response transformer's result for spotify")
    void handleNameSearch_return_result_spotify() throws JsonProcessingException {
      // given
      var jsonMapper = new JsonMapper();
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
      String searchResponse = validatableResponse.extract().asString();
      var expectedSearchResultAsJson = jsonMapper.writeValueAsString(expectedSearchResult);
      assertThat(searchResponse).isEqualTo(expectedSearchResultAsJson);
    }

    @ParameterizedTest
    @MethodSource("faultyInputQueryProvider")
    @DisplayName("Should return empty response for empty or missing query")
    void test_faulty_query(String query) throws JsonProcessingException {
      // given
      var jsonMapper = new JsonMapper();
      var expectedSearchResult = ArtistSearchResponse.empty();
      Map<String, Object> requestParams = Map.of(
          "query", query,
          "page", DEFAULT_PAGE,
          "size", DEFAULT_SIZE
      );

      try(MockedStatic<ArtistSearchResponse> mock = Mockito.mockStatic(ArtistSearchResponse.class)) {
        mock.when(ArtistSearchResponse::empty).thenReturn(expectedSearchResult);

        // when
        var validatableResponse = restAssuredUtils.doGet(requestParams);

        // then
        mock.verify(ArtistSearchResponse::empty);

        String searchResponse = validatableResponse.extract().asString();
        var expectedSearchResultAsJson = jsonMapper.writeValueAsString(expectedSearchResult);
        assertThat(searchResponse).isEqualTo(expectedSearchResultAsJson);
      }
    }

    private Stream<Arguments> faultyInputQueryProvider() {
      return Stream.of(
          Arguments.of(""),
          Arguments.of("   ")
      );
    }
  }

  @Nested
  @TestInstance(PER_CLASS)
  @DisplayName("Test follow/unfollow endpoints")
  class FollowArtistTest {

    private RestAssuredMockMvcUtils followArtistRestAssuredUtils;
    private RestAssuredMockMvcUtils unfollowArtistRestAssuredUtils;

    @BeforeEach
    void setUp() {
      followArtistRestAssuredUtils = new RestAssuredMockMvcUtils(FOLLOW_ARTIST);
      unfollowArtistRestAssuredUtils = new RestAssuredMockMvcUtils(UNFOLLOW_ARTIST);
      RestAssuredMockMvc.standaloneSetup(underTest, RestExceptionsHandler.class);
    }

    @AfterEach
    void tearDown() {
      reset(followArtistService);
    }

    @Test
    @DisplayName("Should return 200 when following an artist")
    void handle_follow_return_200() {
      // given
      var url = "/" + VALID_SOURCE_STRING + "/" + VALID_EXTERNAL_ID;

      // when
      var validatableResponse = followArtistRestAssuredUtils.doPost(url);

      // then
      validatableResponse.statusCode(OK.value());
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
      result.status(UNPROCESSABLE_ENTITY);
    }

    @Test
    @DisplayName("Should return 200 when unfollowing an artist")
    void handle_unfollow_return_200() {
      // given
      var url = "/" + VALID_SOURCE_STRING + "/" + VALID_EXTERNAL_ID;

      // when
      var validatableResponse = unfollowArtistRestAssuredUtils.doPost(url);

      // then
      validatableResponse.statusCode(OK.value());
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
      result.status(UNPROCESSABLE_ENTITY);
    }
  }

  @Nested
  @DisplayName("Test fetch top artist endpoint")
  class FetchTopArtistsTest {

    @BeforeEach
    void setUp() {
      restAssuredUtils = new RestAssuredMockMvcUtils(TOP_ARTISTS);
      RestAssuredMockMvc.standaloneSetup(underTest, RestExceptionsHandler.class);
    }

    @AfterEach
    void tearDown() {
      reset(artistCollector);
    }

    @Test
    @DisplayName("should call artist collector")
    void should_call_artist_collector() {
      // given
      var minFollower = 10;
      Map<String, Object> requestParams = new HashMap<>();
      requestParams.put("minFollower", minFollower);

      // when
      restAssuredUtils.doGet(requestParams);

      // then
      verify(artistCollector).collectTopFollowedArtists(minFollower);
    }

    @Test
    @DisplayName("should return limited result of artist collector")
    void should_return_result_of_artist_collector() {
      // given
      var limit = 1;
      Map<String, Object> requestParams = new HashMap<>();
      requestParams.put("limit", limit);
      var artist1 = ArtistDtoFactory.withName("A");
      var artist2 = ArtistDtoFactory.withName("B");
      doReturn(List.of(artist1, artist2)).when(artistCollector).collectTopFollowedArtists(anyInt());

      // when
      var validatableResponse = restAssuredUtils.doGet(requestParams);

      // then
      var responseBody = validatableResponse.extract().as(ArtistDto[].class);
      assertThat(responseBody).containsExactly(artist1);
    }

    @Test
    @DisplayName("should return status code 200")
    void should_return_200() {
      // when
      var validatableResponse = restAssuredUtils.doGet();

      // then
      validatableResponse.statusCode(OK.value());
    }
  }
}
