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
import rocks.metaldetector.config.constants.Endpoints;
import rocks.metaldetector.service.SlicingService;
import rocks.metaldetector.service.artist.ArtistDto;
import rocks.metaldetector.service.artist.FollowArtistService;
import rocks.metaldetector.testutil.DtoFactory.ArtistDtoFactory;
import rocks.metaldetector.web.RestAssuredMockMvcUtils;
import rocks.metaldetector.web.api.response.MyArtistsResponse;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@ExtendWith(MockitoExtension.class)
class MyArtistsRestControllerTest implements WithAssertions {

  private static final int PAGE = 0;
  private static final int SIZE = 10;

  @Mock
  private FollowArtistService followArtistService;

  @Mock
  private SlicingService slicingService;

  @InjectMocks
  private MyArtistsRestController underTest;

  private RestAssuredMockMvcUtils restAssuredMockMvcUtils;

  @BeforeEach
  void setUp() {
    restAssuredMockMvcUtils = new RestAssuredMockMvcUtils(Endpoints.Rest.MY_ARTISTS);
    RestAssuredMockMvc.standaloneSetup(underTest,
                                       springSecurity((request, response, chain) -> chain.doFilter(request, response)));
  }

  @AfterEach
  void tearDown() {
    reset(followArtistService, slicingService);
  }

  @Test
  @DisplayName("GET should return 200")
  void get_should_return_200() {
    // given
    doReturn(List.of(ArtistDtoFactory.createDefault())).when(followArtistService).getFollowedArtistsOfCurrentUser();

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
    verify(followArtistService, times(1)).getFollowedArtistsOfCurrentUser();
  }

  @Test
  @DisplayName("GET should call slicingService")
  void get_should_call_slicing_service() {
    // given
    var artists = List.of(ArtistDtoFactory.createDefault());
    doReturn(artists).when(followArtistService).getFollowedArtistsOfCurrentUser();

    // when
    restAssuredMockMvcUtils.doGet(Map.of("page", PAGE, "size", SIZE));

    // then
    verify(slicingService, times(1)).slice(artists, PAGE, SIZE);
  }

  @Test
  @DisplayName("GET should return sliced results if present")
  void get_should_return_results() {
    ArtistDto artistDto = ArtistDtoFactory.createDefault();
    doReturn(List.of(artistDto)).when(slicingService).slice(any(), anyInt(), anyInt());

    // when
    ValidatableMockMvcResponse validatableResponse = restAssuredMockMvcUtils.doGet(Map.of("page", PAGE, "size", SIZE));

    // then
    MyArtistsResponse response = validatableResponse.extract().as(MyArtistsResponse.class);

    assertThat(response).isNotNull();
    assertThat(response.getMyArtists()).hasSize(1);
    assertThat(response.getMyArtists().get(0)).isEqualTo(artistDto);
  }

  @Test
  @DisplayName("GET should return pagination")
  void get_should_return_pagination() {
    // given
    doReturn(List.of(ArtistDtoFactory.createDefault())).when(followArtistService).getFollowedArtistsOfCurrentUser();

    // when
    ValidatableMockMvcResponse validatableResponse = restAssuredMockMvcUtils.doGet(Map.of("page", PAGE, "size", SIZE));

    // then
    MyArtistsResponse response = validatableResponse.extract().as(MyArtistsResponse.class);

    assertThat(response.getPagination()).isNotNull();
    assertThat(response.getPagination().getTotalPages()).isEqualTo(1);
    assertThat(response.getPagination().getItemsPerPage()).isEqualTo(SIZE);
    assertThat(response.getPagination().getCurrentPage()).isEqualTo(PAGE);
  }

  @Test
  @DisplayName("GET should return empty list if nothing is present")
  void get_should_return_empty_list() {
    // given
    doReturn(Collections.emptyList()).when(followArtistService).getFollowedArtistsOfCurrentUser();

    // when
    ValidatableMockMvcResponse validatableResponse = restAssuredMockMvcUtils.doGet(Map.of("page", PAGE, "size", SIZE));

    // then
    MyArtistsResponse response = validatableResponse.extract().as(MyArtistsResponse.class);

    assertThat(response).isNotNull();
    assertThat(response.getMyArtists()).isEmpty();
  }

  @Test
  @DisplayName("GET should return empty pagination if nothing is present")
  void get_should_return_empty_pagination() {
    // given
    doReturn(Collections.emptyList()).when(followArtistService).getFollowedArtistsOfCurrentUser();

    // when
    ValidatableMockMvcResponse validatableResponse = restAssuredMockMvcUtils.doGet(Map.of("page", PAGE, "size", SIZE));

    // then
    MyArtistsResponse response = validatableResponse.extract().as(MyArtistsResponse.class);

    assertThat(response.getPagination()).isNotNull();
    assertThat(response.getPagination().getTotalPages()).isEqualTo(0);
    assertThat(response.getPagination().getItemsPerPage()).isEqualTo(SIZE);
    assertThat(response.getPagination().getCurrentPage()).isEqualTo(PAGE);
  }
}