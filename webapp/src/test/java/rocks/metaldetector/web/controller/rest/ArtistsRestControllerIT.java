package rocks.metaldetector.web.controller.rest;

import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import rocks.metaldetector.config.constants.Endpoints;
import rocks.metaldetector.service.artist.ArtistsService;
import rocks.metaldetector.testutil.WithIntegrationTestConfig;
import rocks.metaldetector.web.DtoFactory.DiscogsArtistSearchResultDtoFactory;
import rocks.metaldetector.web.RestAssuredRequestHandler;
import rocks.metaldetector.support.Pagination;
import rocks.metaldetector.web.api.response.SearchResponse;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(MockitoExtension.class)
class ArtistsRestControllerIT implements WithAssertions, WithIntegrationTestConfig {

  private static final long VALID_ARTIST_ID = 252211L;
  private static final long INVALID_ARTIST_ID = 0L;
  private static final String VALID_SEARCH_REQUEST = "Darkthrone";

  @MockBean
  private ArtistsService artistsService;

  @LocalServerPort
  private int port;

  private RestAssuredRequestHandler requestHandler;

  @Nested
  @TestInstance(TestInstance.Lifecycle.PER_CLASS)
  @DisplayName("Test artist name search endpoint")
  class ArtistNameSearchTest {

    private static final int DEFAULT_PAGE = 1;
    private static final int DEFAULT_SIZE = 10;
    private static final int TOTAL_PAGES = 1;

    private final String requestUri = "http://localhost:" + port + Endpoints.Rest.ARTISTS + Endpoints.Rest.SEARCH;

    @BeforeEach
    void setUp() {
      requestHandler = new RestAssuredRequestHandler(requestUri);
    }

    @AfterEach
    void tearDown() {
      reset(artistsService);
    }

    @Test
    @DisplayName("GET with valid request should return 200")
    void get_with_valid_request_should_return_200() {
      // given
      Map<String, Object> requestParams = new HashMap<>();
      requestParams.put("query", VALID_SEARCH_REQUEST);
      requestParams.put("page", DEFAULT_PAGE);
      requestParams.put("size", DEFAULT_SIZE);

      when(artistsService.searchDiscogsByName(VALID_SEARCH_REQUEST, PageRequest.of(DEFAULT_PAGE, DEFAULT_SIZE)))
          .thenReturn(DiscogsArtistSearchResultDtoFactory.createDefault());

      // when
      ValidatableResponse validatableResponse = requestHandler.doGet(ContentType.JSON, requestParams);

      // then
      validatableResponse
          .contentType(ContentType.JSON)
          .statusCode(HttpStatus.OK.value());

      SearchResponse searchResponse = validatableResponse.extract().as(SearchResponse.class);
      assertThat(searchResponse.getSearchResults()).isNotNull().hasSize(1);

      SearchResponse.SearchResult searchResult = searchResponse.getSearchResults().get(0);
      assertThat(searchResult).isEqualTo(new SearchResponse.SearchResult(null, VALID_ARTIST_ID, VALID_SEARCH_REQUEST, false));

      Pagination pagination = searchResponse.getPagination();
      assertThat(pagination).isEqualTo(new Pagination(TOTAL_PAGES, DEFAULT_PAGE, DEFAULT_SIZE));

      verify(artistsService, times(1)).searchDiscogsByName(VALID_SEARCH_REQUEST, PageRequest.of(DEFAULT_PAGE, DEFAULT_SIZE));
    }

    // ToDo DanielW: Der Test ist so eigentlich nicht notwendig, da es normal ist, dass zu einer Suche nichts gefunden wird...ist also 200 OK.
    //  Zu testen gilt es noch, ob Discogs sich auch korrekt verhält, wenn zu einer Suche nichts gefunden wird
//    @Test
//    @DisplayName("GET with empty result should return 404")
//    void get_with_empty_result_should_return_404() {
//      // given
//      Map<String, Object> requestParams = new HashMap<>();
//      requestParams.put("query", NO_RESULT_SEARCH_REQUEST);
//      requestParams.put("page", DEFAULT_PAGE);
//      requestParams.put("size", DEFAULT_SIZE);
//
//      when(artistsService.searchDiscogsByName(NO_RESULT_SEARCH_REQUEST, PageRequest.of(DEFAULT_PAGE, DEFAULT_SIZE)))
//          .thenReturn(Optional.empty());
//
//      // when
//      ValidatableResponse validatableResponse = requestHandler.doGet(ContentType.JSON, requestParams);
//
//      // then
//      validatableResponse.statusCode(HttpStatus.NOT_FOUND.value());
//      verify(artistsService, times(1)).searchDiscogsByName(NO_RESULT_SEARCH_REQUEST, PageRequest.of(DEFAULT_PAGE, DEFAULT_SIZE));
//    }
  }

  @Nested
  @TestInstance(TestInstance.Lifecycle.PER_CLASS)
  @DisplayName("Test follow/unfollow endpoints")
  class FollowArtistTest {

    private RestAssuredRequestHandler followRequestHandler;
    private RestAssuredRequestHandler unfollowRequestHandler;

    private final String followRequestUri   = "http://localhost:" + port + Endpoints.Rest.ARTISTS + Endpoints.Rest.FOLLOW;
    private final String unfollowRequestUri = "http://localhost:" + port + Endpoints.Rest.ARTISTS + Endpoints.Rest.UNFOLLOW;

    @BeforeEach
    void setUp() {
      followRequestHandler    = new RestAssuredRequestHandler(followRequestUri);
      unfollowRequestHandler  = new RestAssuredRequestHandler(unfollowRequestUri);
    }

    @AfterEach
    void tearDown() {
      reset(artistsService);
    }

    @Test
    @DisplayName("CREATE with valid request should create an entity")
    void create_with_valid_request_should_return_201() {
      // given
      when(artistsService.followArtist(VALID_ARTIST_ID)).thenReturn(true);

      // when
      ValidatableResponse validatableResponse = followRequestHandler.doPost("/" + VALID_ARTIST_ID, ContentType.JSON);

      // then
      validatableResponse.statusCode(HttpStatus.OK.value());
      verify(artistsService, times(1)).followArtist(VALID_ARTIST_ID);
    }

    @Test
    @DisplayName("CREATE should return 404 if the artist is not found")
    void create_should_return_404_if_artist_not_found() {
      // given
      when(artistsService.followArtist(INVALID_ARTIST_ID)).thenReturn(false);

      // when
      ValidatableResponse validatableResponse = followRequestHandler.doPost("/" + INVALID_ARTIST_ID, ContentType.JSON);

      // then
      validatableResponse.statusCode(HttpStatus.NOT_FOUND.value());
      verify(artistsService, times(1)).followArtist(INVALID_ARTIST_ID);
    }

    @Test
    @DisplayName("DELETE should should delete the entity if it exists")
    void delete_an_existing_resource_should_return_200() {
      // given
      when(artistsService.unfollowArtist(VALID_ARTIST_ID)).thenReturn(true);

      // when
      ValidatableResponse validatableResponse = unfollowRequestHandler.doPost("/" + VALID_ARTIST_ID, ContentType.JSON);

      // then
      validatableResponse.statusCode(HttpStatus.OK.value());
      verify(artistsService, times(1)).unfollowArtist(VALID_ARTIST_ID);
    }

    @Test
    @DisplayName("DELETE should should return 404 if the entity does not exist")
    void delete_an_not_existing_resource_should_return_404() {
      // given
      when(artistsService.unfollowArtist(VALID_ARTIST_ID)).thenReturn(false);

      // when
      ValidatableResponse validatableResponse = unfollowRequestHandler.doPost("/" + VALID_ARTIST_ID, ContentType.JSON);

      // then
      validatableResponse.statusCode(HttpStatus.NOT_FOUND.value());
      verify(artistsService, times(1)).unfollowArtist(VALID_ARTIST_ID);
    }
  }
}
