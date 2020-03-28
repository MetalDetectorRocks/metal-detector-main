package rocks.metaldetector.web.controller.rest;

import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import rocks.metaldetector.config.constants.Endpoints;
import rocks.metaldetector.discogs.facade.dto.DiscogsArtistSearchResultDto;
import rocks.metaldetector.service.artist.ArtistsService;
import rocks.metaldetector.testutil.DtoFactory.DiscogsArtistSearchResultDtoFactory;
import rocks.metaldetector.testutil.WithExceptionResolver;
import rocks.metaldetector.web.RestAssuredMockMvcUtils;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@ExtendWith(MockitoExtension.class)
class ArtistsRestControllerTest implements WithAssertions, WithExceptionResolver {

  private static final long VALID_ARTIST_ID = 252211L;
  private static final String VALID_SEARCH_REQUEST = "Darkthrone";

  @Mock
  private ArtistsService artistsService;

  @InjectMocks
  private ArtistsRestController underTest;

  private RestAssuredMockMvcUtils restAssuredUtils;

  @Nested
  @TestInstance(TestInstance.Lifecycle.PER_CLASS)
  @DisplayName("Test artist name search endpoint")
  class ArtistNameSearchTest {

    private static final int DEFAULT_PAGE = 1;
    private static final int DEFAULT_SIZE = 10;

    @BeforeEach
    void setUp() {
      restAssuredUtils = new RestAssuredMockMvcUtils(Endpoints.Rest.ARTISTS + Endpoints.Rest.SEARCH);
      RestAssuredMockMvc.standaloneSetup(underTest,
                                         springSecurity((request, response, chain) -> chain.doFilter(request, response)),
                                         exceptionResolver());
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
      restAssuredUtils.doGet(requestParams);

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
      var validatableResponse = restAssuredUtils.doGet(requestParams);

      // then
      validatableResponse
              .contentType(ContentType.JSON)
              .statusCode(HttpStatus.OK.value());

      DiscogsArtistSearchResultDto searchResponse = validatableResponse.extract().as(DiscogsArtistSearchResultDto.class);
      assertThat(searchResponse).isEqualTo(expectedSearchResult);
    }
//    @Test
//    @DisplayName("GET with valid request should return 200")
//    void get_with_valid_request_should_return_200() {
//      // given
//      Map<String, Object> requestParams = new HashMap<>();
//      requestParams.put("query", VALID_SEARCH_REQUEST);
//      requestParams.put("page", DEFAULT_PAGE);
//      requestParams.put("size", DEFAULT_SIZE);
//
//      when(artistsService.searchDiscogsByName(VALID_SEARCH_REQUEST, PageRequest.of(DEFAULT_PAGE, DEFAULT_SIZE)))
//          .thenReturn(Optional.of(ArtistNameSearchResponseFactory.withOneResult()));
//
//      // when
//      ValidatableMockMvcResponse validatableResponse = restAssuredUtils.doGet(requestParams);
//
//      // then
//      validatableResponse
//          .contentType(ContentType.JSON)
//          .statusCode(HttpStatus.OK.value());
//
//      SearchResponse searchResponse = validatableResponse.extract().as(SearchResponse.class);
//      assertThat(searchResponse.getSearchResults()).isNotNull().hasSize(1);
//
//      SearchResponse.SearchResult searchResult = searchResponse.getSearchResults().get(0);
//      assertThat(searchResult).isEqualTo(new SearchResponse.SearchResult(null, VALID_ARTIST_ID, VALID_SEARCH_REQUEST, false));
//
//      Pagination pagination = searchResponse.getPagination();
//      assertThat(pagination).isEqualTo(new Pagination(TOTAL_PAGES, DEFAULT_PAGE, DEFAULT_SIZE));
//
//      verify(artistsService, times(1)).searchDiscogsByName(VALID_SEARCH_REQUEST, PageRequest.of(DEFAULT_PAGE, DEFAULT_SIZE));
//    }
//
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
//      ValidatableMockMvcResponse validatableResponse = restAssuredUtils.doGet(requestParams);
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

    private RestAssuredMockMvcUtils followArtistRestAssuredUtils;
    private RestAssuredMockMvcUtils unfollowArtistRestAssuredUtils;

    @BeforeEach
    void setUp() {
      followArtistRestAssuredUtils  = new RestAssuredMockMvcUtils(Endpoints.Rest.ARTISTS + Endpoints.Rest.FOLLOW);
      unfollowArtistRestAssuredUtils  = new RestAssuredMockMvcUtils(Endpoints.Rest.ARTISTS + Endpoints.Rest.UNFOLLOW);
      RestAssuredMockMvc.standaloneSetup(underTest,
                                         springSecurity((request, response, chain) -> chain.doFilter(request, response)),
                                         exceptionResolver());
    }

    @AfterEach
    void tearDown() {
      reset(artistsService);
    }

    @Test
    @DisplayName("Should call artist service when following an artist and return 200")
    void handleFollow() {
      // when
      var validatableResponse = followArtistRestAssuredUtils.doPost("/" + VALID_ARTIST_ID);

      // then
      validatableResponse.statusCode(HttpStatus.OK.value());
      verify(artistsService, times(1)).followArtist(VALID_ARTIST_ID);
    }

    @Test
    @DisplayName("Should call artist service when unfollowing an artist and return 200")
    void handleUnfollow() {
      // when
      var validatableResponse = unfollowArtistRestAssuredUtils.doPost("/" + VALID_ARTIST_ID);

      // then
      validatableResponse.statusCode(HttpStatus.OK.value());
      verify(artistsService, times(1)).unfollowArtist(VALID_ARTIST_ID);
    }

//    @Test
//    @DisplayName("CREATE with valid request should create an entity")
//    void create_with_valid_request_should_return_201() {
//      // given
//      when(artistsService.followArtist(VALID_ARTIST_ID)).thenReturn(true);
//
//      // when
//      ValidatableMockMvcResponse validatableResponse = followArtistRestAssuredUtils.doPost("/" + VALID_ARTIST_ID);
//
//      // then
//      validatableResponse.statusCode(HttpStatus.OK.value());
//      verify(artistsService, times(1)).followArtist(VALID_ARTIST_ID);
//    }
//
//    @Test
//    @DisplayName("CREATE should return 404 if the artist is not found")
//    void create_should_return_404_if_artist_not_found() {
//      // given
//      when(artistsService.followArtist(INVALID_ARTIST_ID)).thenReturn(false);
//
//      // when
//      ValidatableMockMvcResponse validatableResponse = followArtistRestAssuredUtils.doPost("/" + INVALID_ARTIST_ID);
//
//      // then
//      validatableResponse.statusCode(HttpStatus.NOT_FOUND.value());
//      verify(artistsService, times(1)).followArtist(INVALID_ARTIST_ID);
//    }
//
//    @Test
//    @DisplayName("DELETE should should delete the entity if it exists")
//    void delete_an_existing_resource_should_return_200() {
//      // given
//      when(artistsService.unfollowArtist(VALID_ARTIST_ID)).thenReturn(true);
//
//      // when
//      ValidatableMockMvcResponse validatableResponse = unfollowArtistRestAssuredUtils.doPost("/" + VALID_ARTIST_ID);
//
//      // then
//      validatableResponse.statusCode(HttpStatus.OK.value());
//      verify(artistsService, times(1)).unfollowArtist(VALID_ARTIST_ID);
//    }
//
//    @Test
//    @DisplayName("DELETE should should return 404 if the entity does not exist")
//    void delete_an_not_existing_resource_should_return_404() {
//      // given
//      when(artistsService.unfollowArtist(VALID_ARTIST_ID)).thenReturn(false);
//
//      // when
//      ValidatableMockMvcResponse validatableResponse = unfollowArtistRestAssuredUtils.doPost("/" + VALID_ARTIST_ID);
//
//      // then
//      validatableResponse.statusCode(HttpStatus.NOT_FOUND.value());
//      verify(artistsService, times(1)).unfollowArtist(VALID_ARTIST_ID);
//    }
  }
}
