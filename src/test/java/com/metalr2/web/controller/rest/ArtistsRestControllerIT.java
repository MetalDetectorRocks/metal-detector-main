package com.metalr2.web.controller.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.metalr2.config.constants.Endpoints;
import com.metalr2.service.artist.ArtistsService;
import com.metalr2.testutil.WithIntegrationTestProfile;
import com.metalr2.web.RestAssuredRequestHandler;
import com.metalr2.web.dto.request.ArtistSearchRequest;
import com.metalr2.web.dto.response.ArtistDetailsResponse;
import com.metalr2.web.dto.response.ArtistNameSearchResponse;
import com.metalr2.web.dto.response.Pagination;
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
import org.springframework.http.HttpStatus;

import java.util.Map;
import java.util.Optional;

import static com.metalr2.web.DtoFactory.ArtistDetailsResponseFactory;
import static com.metalr2.web.DtoFactory.ArtistNameSearchResponseFactory;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(MockitoExtension.class)
class ArtistsRestControllerIT implements WithAssertions, WithIntegrationTestProfile {

  private static final long VALID_ARTIST_ID         = 252211L;
  private static final long INVALID_ARTIST_ID       = 0L;
  private static final String VALID_SEARCH_REQUEST  = "Darkthrone";

  @MockBean
  private ArtistsService artistsService;

  @LocalServerPort
  private int port;

  private RestAssuredRequestHandler requestHandler;
  private ObjectMapper mapper;

  @Nested
  @TestInstance(TestInstance.Lifecycle.PER_CLASS)
  @DisplayName("Test artist details search endpoint")
  class ArtistDetailsSearchTest {

    private final String requestUri = "http://localhost:" + port + Endpoints.Rest.ARTISTS;

    @BeforeEach
    void setUp() {
      requestHandler = new RestAssuredRequestHandler(requestUri);
      mapper = new ObjectMapper();
    }

    @AfterEach
    void tearDown() {
      reset(artistsService);
    }

    @Test
    @DisplayName("GET with valid request should return 200")
    void get_with_valid_request_should_return_200() {
      // given
      when(artistsService.searchDiscogsById(VALID_ARTIST_ID)).thenReturn(Optional.of(ArtistDetailsResponseFactory.withResult()));

      // when
      ValidatableResponse validatableResponse = requestHandler.doGet("/" + VALID_ARTIST_ID, ContentType.JSON);

      // then
      validatableResponse
          .contentType(ContentType.JSON)
          .statusCode(HttpStatus.OK.value());

      ArtistDetailsResponse response = validatableResponse.extract().as(ArtistDetailsResponse.class);
      assertThat(response).isNotNull();
      assertThat(response.getArtistId()).isEqualTo(VALID_ARTIST_ID);

      verify(artistsService, times(1)).searchDiscogsById(VALID_ARTIST_ID);
    }

    @Test
    @DisplayName("GET with no results should return 404")
    void get_with_no_results_should_return_404() {
      // given
      when(artistsService.searchDiscogsById(INVALID_ARTIST_ID)).thenReturn(Optional.empty());

      // when
      ValidatableResponse validatableResponse = requestHandler.doGet("/" + INVALID_ARTIST_ID, ContentType.JSON);

      // then
      validatableResponse.statusCode(HttpStatus.NOT_FOUND.value());
      verify(artistsService, times(1)).searchDiscogsById(INVALID_ARTIST_ID);
    }
  }

  @Nested
  @TestInstance(TestInstance.Lifecycle.PER_CLASS)
  @DisplayName("Test artist name search endpoint")
  class ArtistNameSearchTest {

    private static final String NO_RESULT_SEARCH_REQUEST  = "NoResult";
    private static final int DEFAULT_PAGE                 = 1;
    private static final int DEFAULT_SIZE                 = 10;
    private static final int TOTAL_PAGES                  = 2;

    private final String requestUri = "http://localhost:" + port + Endpoints.Rest.ARTISTS + Endpoints.Rest.SEARCH;

    @BeforeEach
    void setUp() {
      requestHandler = new RestAssuredRequestHandler(requestUri);
      mapper = new ObjectMapper();
    }

    @AfterEach
    void tearDown() {
      reset(artistsService);
    }

    @Test
    @DisplayName("GET with valid request should return 200")
    void get_with_valid_request_should_return_200() {
      // given
      ArtistSearchRequest request = new ArtistSearchRequest(VALID_SEARCH_REQUEST, DEFAULT_PAGE, DEFAULT_SIZE);
      Map<String, Object> requestParams = mapper.convertValue(request, new TypeReference<Map<String, Object>>() {});

      when(artistsService.searchDiscogsByName(request.getArtistName(), request.getPage(), request.getSize()))
          .thenReturn(Optional.of(ArtistNameSearchResponseFactory.withOneResult()));

      // when
      ValidatableResponse validatableResponse = requestHandler.doGet(ContentType.JSON, requestParams);

      // then
      validatableResponse
          .contentType(ContentType.JSON)
          .statusCode(HttpStatus.OK.value());

      ArtistNameSearchResponse artistNameSearchResponse = validatableResponse.extract().as(ArtistNameSearchResponse.class);
      assertThat(artistNameSearchResponse.getArtistSearchResults()).isNotNull().hasSize(1);

      ArtistNameSearchResponse.ArtistSearchResult artistSearchResult = artistNameSearchResponse.getArtistSearchResults().get(0);
      assertThat(artistSearchResult).isEqualTo(new ArtistNameSearchResponse.ArtistSearchResult(null, VALID_ARTIST_ID, VALID_SEARCH_REQUEST, false));

      Pagination pagination = artistNameSearchResponse.getPagination();
      assertThat(pagination).isEqualTo(new Pagination(TOTAL_PAGES, DEFAULT_PAGE, DEFAULT_SIZE));

      verify(artistsService, times(1)).searchDiscogsByName(request.getArtistName(), request.getPage(), request.getSize());
    }

    @Test
    @DisplayName("GET with bad request should return 400")
    void get_with_bad_request_should_return_400() {
      // given
      ArtistSearchRequest request = new ArtistSearchRequest(null, DEFAULT_PAGE, DEFAULT_SIZE);
      Map<String, Object> requestParams = mapper.convertValue(request, new TypeReference<Map<String, Object>>() {});

      // when
      ValidatableResponse validatableResponse = requestHandler.doGet(ContentType.JSON, requestParams);

      // then
      validatableResponse.statusCode(HttpStatus.BAD_REQUEST.value());
      verify(artistsService, times(0)).searchDiscogsByName(request.getArtistName(), request.getPage(), request.getSize());
    }

    @Test
    @DisplayName("GET with empty result should return 404")
    void get_with_empty_result_should_return_404() {
      // given
      ArtistSearchRequest request = new ArtistSearchRequest(NO_RESULT_SEARCH_REQUEST, DEFAULT_PAGE, DEFAULT_SIZE);
      Map<String, Object> requestParams = mapper.convertValue(request, new TypeReference<Map<String, Object>>() {});

      when(artistsService.searchDiscogsByName(request.getArtistName(), request.getPage(), request.getSize()))
          .thenReturn(Optional.empty());

      // when
      ValidatableResponse validatableResponse = requestHandler.doGet(ContentType.JSON, requestParams);

      // then
      validatableResponse.statusCode(HttpStatus.NOT_FOUND.value());
      verify(artistsService, times(1)).searchDiscogsByName(request.getArtistName(), request.getPage(), request.getSize());
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
      mapper                  = new ObjectMapper();
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
