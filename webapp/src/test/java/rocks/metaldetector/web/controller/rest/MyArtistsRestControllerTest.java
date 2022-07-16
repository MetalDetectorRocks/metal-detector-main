package rocks.metaldetector.web.controller.rest;

import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import io.restassured.module.mockmvc.response.ValidatableMockMvcResponse;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import rocks.metaldetector.service.artist.ArtistDto;
import rocks.metaldetector.service.artist.FollowArtistService;
import rocks.metaldetector.support.Endpoints;
import rocks.metaldetector.support.Pagination;
import rocks.metaldetector.testutil.DtoFactory.ArtistDtoFactory;
import rocks.metaldetector.web.RestAssuredMockMvcUtils;
import rocks.metaldetector.web.api.response.MyArtistsResponse;
import rocks.metaldetector.web.transformer.MyArtistsResponseTransformer;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MyArtistsRestControllerTest implements WithAssertions {

  private static final int PAGE = 0;
  private static final int SIZE = 10;

  @Mock
  private FollowArtistService followArtistService;

  @Mock
  private MyArtistsResponseTransformer responseTransformer;

  @InjectMocks
  private MyArtistsRestController underTest;

  private RestAssuredMockMvcUtils restAssuredMockMvcUtils;

  @BeforeEach
  void setUp() {
    restAssuredMockMvcUtils = new RestAssuredMockMvcUtils(Endpoints.Rest.MY_ARTISTS);
    RestAssuredMockMvc.standaloneSetup(underTest);
  }

  @AfterEach
  void tearDown() {
    reset(followArtistService, responseTransformer);
  }

  @Test
  @DisplayName("GET should return 200")
  void get_should_return_200() {
    // given
    doReturn(new MyArtistsResponse()).when(responseTransformer).transform(anyList(), anyInt(), anyInt());

    // when
    ValidatableMockMvcResponse validatableResponse = restAssuredMockMvcUtils.doGet(Map.of("page", PAGE, "size", SIZE));

    // then
    validatableResponse
        .contentType(ContentType.JSON)
        .statusCode(HttpStatus.OK.value());
  }

  @Test
  @DisplayName("GET should call followArtistService")
  void get_should_call_follow_artist_service() {
    // given
    doReturn(Collections.emptyList()).when(followArtistService).getFollowedArtistsOfCurrentUser();

    // when
    restAssuredMockMvcUtils.doGet(Map.of("page", PAGE, "size", SIZE));

    // then
    verify(followArtistService).getFollowedArtistsOfCurrentUser();
  }

  @Test
  @DisplayName("GET should call responseTransformer")
  void get_should_call_response_transformer() {
    // given
    var artists = List.of(ArtistDtoFactory.createDefault());
    doReturn(artists).when(followArtistService).getFollowedArtistsOfCurrentUser();

    // when
    restAssuredMockMvcUtils.doGet(Map.of("page", PAGE, "size", SIZE));

    // then
    verify(responseTransformer).transform(artists, PAGE, SIZE);
  }

  @Test
  @DisplayName("GET should return results")
  void get_should_return_results() {
    ArtistDto artistDto = ArtistDtoFactory.createDefault();
    MyArtistsResponse expectedResponse = new MyArtistsResponse(List.of(artistDto), new Pagination());
    doReturn(expectedResponse).when(responseTransformer).transform(anyList(), anyInt(), anyInt());

    // when
    ValidatableMockMvcResponse validatableResponse = restAssuredMockMvcUtils.doGet(Map.of("page", PAGE, "size", SIZE));

    // then
    MyArtistsResponse response = validatableResponse.extract().as(MyArtistsResponse.class);

    assertThat(response).isEqualTo(expectedResponse);
  }
}