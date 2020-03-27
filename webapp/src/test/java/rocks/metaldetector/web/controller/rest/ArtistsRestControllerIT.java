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
import rocks.metaldetector.discogs.facade.dto.DiscogsArtistSearchResultDto;
import rocks.metaldetector.service.artist.ArtistsService;
import rocks.metaldetector.testutil.WithIntegrationTestConfig;
import rocks.metaldetector.testutil.DtoFactory.DiscogsArtistSearchResultDtoFactory;
import rocks.metaldetector.web.RestAssuredRequestHandler;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(MockitoExtension.class)
class ArtistsRestControllerIT implements WithAssertions, WithIntegrationTestConfig {

  private static final long VALID_ARTIST_ID = 252211L;
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
    @DisplayName("Should pass request parameter to artist service")
    void handleNameSearch_pass_arguments() {
      // given
      Map<String, Object> requestParams = new HashMap<>();
      requestParams.put("query", VALID_SEARCH_REQUEST);
      requestParams.put("page", DEFAULT_PAGE);
      requestParams.put("size", DEFAULT_SIZE);

      // when
      requestHandler.doGet(ContentType.JSON, requestParams);

      // then
      verify(artistsService, times(1)).searchDiscogsByName(VALID_SEARCH_REQUEST, PageRequest.of(DEFAULT_PAGE, DEFAULT_SIZE));
    }

    @Test
    @DisplayName("Should return results from artist service with status code 200")
    void handleNameSearch_return_result() {
      // given
      var expectedSearchResult = DiscogsArtistSearchResultDtoFactory.createDefault();
      Map<String, Object> requestParams = Map.of(
              "query", VALID_SEARCH_REQUEST,
              "page", DEFAULT_PAGE,
              "size", DEFAULT_SIZE
      );
      doReturn(expectedSearchResult).when(artistsService).searchDiscogsByName(VALID_SEARCH_REQUEST, PageRequest.of(DEFAULT_PAGE, DEFAULT_SIZE));

      // when
      ValidatableResponse validatableResponse = requestHandler.doGet(ContentType.JSON, requestParams);

      // then
      validatableResponse
          .contentType(ContentType.JSON)
          .statusCode(HttpStatus.OK.value());

      DiscogsArtistSearchResultDto searchResponse = validatableResponse.extract().as(DiscogsArtistSearchResultDto.class);
      assertThat(searchResponse).isEqualTo(expectedSearchResult);
    }
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
    @DisplayName("Should call artist service when following an artist and return 200")
    void handleFollow() {
      // when
      ValidatableResponse validatableResponse = followRequestHandler.doPost("/" + VALID_ARTIST_ID, ContentType.JSON);

      // then
      validatableResponse.statusCode(HttpStatus.OK.value());
      verify(artistsService, times(1)).followArtist(VALID_ARTIST_ID);
    }

    @Test
    @DisplayName("Should call artist service when unfollowing an artist and return 200")
    void handleUnfollow() {
      // when
      ValidatableResponse validatableResponse = unfollowRequestHandler.doPost("/" + VALID_ARTIST_ID, ContentType.JSON);

      // then
      validatableResponse.statusCode(HttpStatus.OK.value());
      verify(artistsService, times(1)).unfollowArtist(VALID_ARTIST_ID);
    }
  }
}
