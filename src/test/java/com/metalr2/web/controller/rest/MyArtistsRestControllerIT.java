package com.metalr2.web.controller.rest;

import com.metalr2.config.constants.Endpoints;
import com.metalr2.service.artist.ArtistsService;
import com.metalr2.testutil.WithIntegrationTestProfile;
import com.metalr2.web.RestAssuredRequestHandler;
import com.metalr2.web.dto.response.MyArtistsResponse;
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

import java.util.HashMap;
import java.util.Map;

import static com.metalr2.web.DtoFactory.MyArtistsResponseFactory;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(MockitoExtension.class)
class MyArtistsRestControllerIT implements WithAssertions, WithIntegrationTestProfile {

  private static final long DISCOGS_ID    = 252211L;
  private static final String ARTIST_NAME = "Darkthrone";

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
    PageRequest pageRequest = PageRequest.of(1,1);
    when(artistsService.findFollowedArtistsForCurrentUser(pageRequest)).thenReturn(MyArtistsResponseFactory.withOneResult());

    // when
    Map<String,Object> params = new HashMap<>();
    params.put("page", 1);
    params.put("size", 1);
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

    verify(artistsService, times(1)).findFollowedArtistsForCurrentUser(pageRequest);
  }

  @Test
  @DisplayName("GET should return 200 and empty list if nothing is present")
  void get_should_return_200_and_empty_list() {
    // given
    PageRequest pageRequest = PageRequest.of(1,1);
    when(artistsService.findFollowedArtistsForCurrentUser(pageRequest)).thenReturn(MyArtistsResponseFactory.withEmptyResult());

    // when
    Map<String,Object> params = new HashMap<>();
    params.put("page", 1);
    params.put("size", 1);
    ValidatableResponse validatableResponse = requestHandler.doGet(ContentType.JSON, params);

    // then
    validatableResponse
        .contentType(ContentType.JSON)
        .statusCode(HttpStatus.OK.value());

    MyArtistsResponse response = validatableResponse.extract().as(MyArtistsResponse.class);

    assertThat(response).isNotNull();
    assertThat(response.getMyArtists()).isEmpty();
    assertThat(response.getPagination()).isNull();

    verify(artistsService, times(1)).findFollowedArtistsForCurrentUser(pageRequest);
  }

}