package rocks.metaldetector.web.controller.rest;

import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import rocks.metaldetector.config.constants.Endpoints;
import rocks.metaldetector.service.artist.ArtistsService;
import rocks.metaldetector.testutil.WithIntegrationTestProfile;
import rocks.metaldetector.web.RestAssuredRequestHandler;
import rocks.metaldetector.web.dto.ArtistDto;
import rocks.metaldetector.web.dto.response.MyArtistsResponse;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(MockitoExtension.class)
class MyArtistsRestControllerIT implements WithAssertions, WithIntegrationTestProfile {

  private static final long DISCOGS_ID = 252211L;
  private static final String ARTIST_NAME = "Darkthrone";
  private static final int PAGE = 0;
  private static final int SIZE = 10;

  @MockBean
  private ArtistsService artistsService;

  @LocalServerPort
  private int port;

  private RestAssuredRequestHandler requestHandler;

  @BeforeEach
  void setUp() {
    String requestUri = "http://localhost:" + port + Endpoints.Rest.MY_ARTISTS;
    requestHandler = new RestAssuredRequestHandler(requestUri);
  }

  @AfterEach
  void tearDown() {
    reset(artistsService);
  }

  @Test
  @DisplayName("GET should return 200 and results if present")
  void get_should_return_200_and_results() {
    // given
    when(artistsService.findFollowedArtistsForCurrentUser(PageRequest.of(PAGE, SIZE))).thenReturn(Collections.singletonList(
        new ArtistDto(DISCOGS_ID, ARTIST_NAME, null)));
    when(artistsService.countFollowedArtistsForCurrentUser()).thenReturn(1L);

    // when
    Map<String, Object> params = new HashMap<>();
    params.put("page", PAGE);
    params.put("size", SIZE);
    ValidatableResponse validatableResponse = requestHandler.doGet(ContentType.JSON, params);

    // then
    validatableResponse
        .contentType(ContentType.JSON)
        .statusCode(HttpStatus.OK.value());

    MyArtistsResponse response = validatableResponse.extract().as(MyArtistsResponse.class);

    assertThat(response).isNotNull();
    assertThat(response.getMyArtists()).hasSize(1);
    assertThat(response.getMyArtists().get(0).getDiscogsId()).isEqualTo(DISCOGS_ID);
    assertThat(response.getMyArtists().get(0).getArtistName()).isEqualTo(ARTIST_NAME);
    assertThat(response.getPagination()).isNotNull();
    assertThat(response.getPagination().getTotalPages()).isEqualTo(1);
    assertThat(response.getPagination().getItemsPerPage()).isEqualTo(SIZE);
    assertThat(response.getPagination().getCurrentPage()).isEqualTo(PAGE);

    verify(artistsService, times(1)).findFollowedArtistsForCurrentUser(PageRequest.of(PAGE, SIZE));
    verify(artistsService, times(1)).countFollowedArtistsForCurrentUser();
  }

  @Test
  @DisplayName("GET should return 200 and empty list if nothing is present")
  void get_should_return_200_and_empty_list() {
    // given
    PageRequest pageRequest = PageRequest.of(PAGE, SIZE);
    when(artistsService.findFollowedArtistsForCurrentUser(pageRequest)).thenReturn(Collections.emptyList());
    when(artistsService.countFollowedArtistsForCurrentUser()).thenReturn(0L);

    // when
    Map<String, Object> params = new HashMap<>();
    params.put("page", PAGE);
    params.put("size", SIZE);
    ValidatableResponse validatableResponse = requestHandler.doGet(ContentType.JSON, params);

    // then
    validatableResponse
        .contentType(ContentType.JSON)
        .statusCode(HttpStatus.OK.value());

    MyArtistsResponse response = validatableResponse.extract().as(MyArtistsResponse.class);

    assertThat(response).isNotNull();
    assertThat(response.getMyArtists()).isEmpty();
    assertThat(response.getPagination()).isNotNull();
    assertThat(response.getPagination().getTotalPages()).isEqualTo(0);
    assertThat(response.getPagination().getItemsPerPage()).isEqualTo(SIZE);
    assertThat(response.getPagination().getCurrentPage()).isEqualTo(PAGE);

    verify(artistsService, times(1)).findFollowedArtistsForCurrentUser(pageRequest);
    verify(artistsService, times(1)).countFollowedArtistsForCurrentUser();
  }
}