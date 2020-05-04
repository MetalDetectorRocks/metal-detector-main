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
import rocks.metaldetector.web.RestAssuredMockMvcUtils;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@ExtendWith(MockitoExtension.class)
class ArtistsRestControllerTest implements WithAssertions {

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
                                         springSecurity((request, response, chain) -> chain.doFilter(request, response)));
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
    @DisplayName("Should return status code 200")
    void handleNameSearch_return_200() {
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
    }

    @Test
    @DisplayName("Should return results from artist service")
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
      DiscogsArtistSearchResultDto searchResponse = validatableResponse.extract().as(DiscogsArtistSearchResultDto.class);
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
      RestAssuredMockMvc.standaloneSetup(underTest,
                                         springSecurity((request, response, chain) -> chain.doFilter(request, response)));
    }

    @AfterEach
    void tearDown() {
      reset(artistsService);
    }

    @Test
    @DisplayName("Should return 200 when following an artist")
    void handle_follow_return_200() {
      // when
      var validatableResponse = followArtistRestAssuredUtils.doPost("/" + VALID_ARTIST_ID);

      // then
      validatableResponse.statusCode(HttpStatus.OK.value());
    }

    // TODO: 04.05.20 tests reparieren
//    @Test
//    @DisplayName("Should call artist service when following an artist")
//    void handle_follow_call_artist_service() {
//      // when
//      followArtistRestAssuredUtils.doPost("/" + VALID_ARTIST_ID);
//
//      // then
//      verify(artistsService, times(1)).followArtist(VALID_ARTIST_ID);
//    }

    @Test
    @DisplayName("Should return 200 when unfollowing an artist")
    void handle_unfollow_return_200() {
      // when
      var validatableResponse = unfollowArtistRestAssuredUtils.doPost("/" + VALID_ARTIST_ID);

      // then
      validatableResponse.statusCode(HttpStatus.OK.value());
    }

//    @Test
//    @DisplayName("Should call artist service when unfollowing an artist")
//    void handle_unfollow_call_artist_service() {
//      // when
//      unfollowArtistRestAssuredUtils.doPost("/" + VALID_ARTIST_ID);
//
//      // then
//      verify(artistsService, times(1)).unfollowArtist(VALID_ARTIST_ID);
//    }
  }
}
