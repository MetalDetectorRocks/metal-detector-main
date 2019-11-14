package com.metalr2.web.controller;

import com.metalr2.config.constants.Endpoints;
import com.metalr2.service.followArtist.FollowArtistService;
import com.metalr2.web.RestAssuredRequestHandler;
import com.metalr2.web.dto.FollowArtistDto;
import com.metalr2.web.dto.request.FollowArtistRequest;
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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test.properties")
@Tag("integration-test")
class FollowArtistRestControllerIT implements WithAssertions {

  private static final String userId        = "1";
  private static final long artistDiscogsId = 252211L;

  @Autowired
  private FollowArtistService followArtistService;
  private FollowArtistDto followArtistDto;

  @Value("${server.address}")
  private String serverAddress;

  @LocalServerPort
  private int port;

  private RestAssuredRequestHandler requestHandler;

  @BeforeEach
  void setUp() {
    String requestUri = "http://" + serverAddress + ":" + port + Endpoints.Rest.FOLLOW_ARTISTS_V1;
    requestHandler = new RestAssuredRequestHandler(requestUri);
    followArtistDto = new FollowArtistDto(userId, artistDiscogsId);
  }

  @Test
  @DisplayName("CREATE should create an entity and return the correct dto")
  void create_with_valid_request_should_return_201() {
    FollowArtistRequest request = new FollowArtistRequest(userId,artistDiscogsId);

    ValidatableResponse validatableResponse = requestHandler.doPost(ContentType.JSON, request);

    FollowArtistDto createdFollowArtistDto = validatableResponse.extract().as(FollowArtistDto.class);

    // assert
    validatableResponse.statusCode(HttpStatus.CREATED.value())
                       .contentType(ContentType.JSON);

    assertThat(createdFollowArtistDto).isNotNull();
    assertThat(followArtistDto.getPublicUserId()).isEqualTo(createdFollowArtistDto.getPublicUserId());
    assertThat(followArtistDto.getArtistDiscogsId()).isEqualTo(createdFollowArtistDto.getArtistDiscogsId());

    // remove created employee
    followArtistService.unfollowArtist(createdFollowArtistDto);
  }

  @Test
  @DisplayName("DELETE should should delete the entity if it exists")
  void delete_an_existing_resource_should_return_200() {
    followArtistService.followArtist(followArtistDto);

    assertThat(followArtistService.followArtistEntityExists(followArtistDto)).isTrue();

    FollowArtistRequest request = new FollowArtistRequest(userId, artistDiscogsId);
    ValidatableResponse validatableResponse = requestHandler.doDelete(ContentType.JSON, request);

    // assert
    validatableResponse.statusCode(HttpStatus.OK.value());
    assertThat(followArtistService.followArtistEntityExists(followArtistDto)).isFalse();
  }

  @Test
  @DisplayName("DELETE should should return 404 if the entity does not exist")
  void delete_an_not_existing_resource_should_return_404() {
    assertThat(followArtistService.followArtistEntityExists(followArtistDto)).isFalse();

    FollowArtistRequest request = new FollowArtistRequest(userId, artistDiscogsId);
    ValidatableResponse validatableResponse = requestHandler.doDelete(ContentType.JSON, request);

    // assert
    validatableResponse.statusCode(HttpStatus.NOT_FOUND.value());
    assertThat(followArtistService.followArtistEntityExists(followArtistDto)).isFalse();
  }
}
