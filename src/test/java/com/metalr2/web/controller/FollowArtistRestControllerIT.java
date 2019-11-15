package com.metalr2.web.controller;

import com.metalr2.config.constants.Endpoints;
import com.metalr2.service.followArtist.FollowArtistService;
import com.metalr2.web.RestAssuredRequestHandler;
import com.metalr2.web.dto.FollowArtistDto;
import com.metalr2.web.dto.request.FollowArtistRequest;
import com.metalr2.web.dto.response.ErrorResponse;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test.properties")
@Tag("integration-test")
class FollowArtistRestControllerIT implements WithAssertions {

  private static final String userId        = "1";
  private static final String artistName    = "Darkthrone";
  private static final long artistDiscogsId = 252211L;

  @Autowired
  private FollowArtistService followArtistService;
  private FollowArtistDto followArtistDto;
  private FollowArtistRequest followArtistRequest;

  @Value("${server.address}")
  private String serverAddress;

  @LocalServerPort
  private int port;

  private RestAssuredRequestHandler requestHandler;

  @BeforeEach
  void setUp() {
    String requestUri   = "http://" + serverAddress + ":" + port + Endpoints.Rest.FOLLOW_ARTISTS_V1;
    requestHandler      = new RestAssuredRequestHandler(requestUri);
    followArtistDto     = new FollowArtistDto(userId, artistName, artistDiscogsId);
    followArtistRequest = new FollowArtistRequest(userId, artistName, artistDiscogsId);
  }

  @Test
  @DisplayName("CREATE with valid request should create an entity and return the correct dto")
  void create_with_valid_request_should_return_201() {
    assertThat(followArtistService.exists(followArtistDto)).isFalse();

    ValidatableResponse validatableResponse = requestHandler.doPost(ContentType.JSON, followArtistRequest);

    // assert
    validatableResponse.statusCode(HttpStatus.CREATED.value());

    assertThat(followArtistService.exists(followArtistDto)).isTrue();

    followArtistService.unfollowArtist(followArtistDto);
  }

  @Test
  @DisplayName("CREATE with invalid request should return 400")
  void create_with_invalid_request_should_return_400() {
    FollowArtistRequest invalidRequest = new FollowArtistRequest(null, null, artistDiscogsId);

    ValidatableResponse validatableResponse = requestHandler.doPost(ContentType.JSON, invalidRequest);
    ErrorResponse errorResponse = validatableResponse.extract().as(ErrorResponse.class);

    // assert
    validatableResponse.statusCode(HttpStatus.BAD_REQUEST.value())
            .contentType(ContentType.JSON);

    assertNotNull(errorResponse);
    assertEquals(2, errorResponse.getMessages().size());
  }

  @Test
  @DisplayName("DELETE should should delete the entity if it exists")
  void delete_an_existing_resource_should_return_200() {
    followArtistService.followArtist(followArtistDto);

    assertThat(followArtistService.exists(followArtistDto)).isTrue();

    FollowArtistRequest request = new FollowArtistRequest(userId, artistName, artistDiscogsId);
    ValidatableResponse validatableResponse = requestHandler.doDelete(ContentType.JSON, request);

    // assert
    validatableResponse.statusCode(HttpStatus.OK.value());
    assertThat(followArtistService.exists(followArtistDto)).isFalse();
  }

  @Test
  @DisplayName("DELETE should should return 404 if the entity does not exist")
  void delete_an_not_existing_resource_should_return_404() {
    assertThat(followArtistService.exists(followArtistDto)).isFalse();

    FollowArtistRequest request = new FollowArtistRequest(userId, artistName, artistDiscogsId);
    ValidatableResponse validatableResponse = requestHandler.doDelete(ContentType.JSON, request);

    // assert
    validatableResponse.statusCode(HttpStatus.NOT_FOUND.value());
    assertThat(followArtistService.exists(followArtistDto)).isFalse();
  }
}
