package com.metalr2.web.controller;

import com.metalr2.config.constants.Endpoints;
import com.metalr2.model.user.UserEntity;
import com.metalr2.model.user.UserRepository;
import com.metalr2.model.user.UserRole;
import com.metalr2.service.followArtist.FollowArtistService;
import com.metalr2.web.RestAssuredRequestHandler;
import com.metalr2.web.dto.FollowArtistDto;
import com.metalr2.web.dto.request.FollowArtistRequest;
import com.metalr2.web.dto.response.ErrorResponse;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.*;
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

  private static final String USER_ID         = "1";
  private static final String ARTIST_NAME     = "Darkthrone";
  private static final long ARTIST_DISCOGS_ID = 252211L;
  private static final String USERNAME        = "JohnD";
  private static final String PASSWORD        = "john.doe";
  private static final String EMAIL           = "john.doe@example.com";

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private FollowArtistService followArtistService;
  private FollowArtistDto followArtistDto;
  private FollowArtistRequest followArtistRequest;

  @Value("${server.address}")
  private String serverAddress;

  @LocalServerPort
  private int port;

  private RestAssuredRequestHandler<FollowArtistRequest> requestHandler;

  @BeforeEach
  void setUp() {
    String requestUri   = "http://" + serverAddress + ":" + port + Endpoints.Rest.FOLLOW_ARTISTS_V1;
    requestHandler      = new RestAssuredRequestHandler<>(requestUri, USERNAME, PASSWORD);
    followArtistDto     = new FollowArtistDto(USER_ID, ARTIST_NAME, ARTIST_DISCOGS_ID);
    followArtistRequest = new FollowArtistRequest(ARTIST_NAME, ARTIST_DISCOGS_ID);
    userRepository.save(UserEntity.builder()
                  .username(USERNAME)
                  .email(EMAIL)
                  .password("$2a$10$2IevDskxEeSmy7Sy41Xl7.u22hTcw3saxQghS.bWaIx3NQrzKTvxK")
                  .enabled(true)
                  .userRoles(UserRole.createUserRole())
                  .build());
  }

  @AfterEach
  void tearDown() {
    userRepository.deleteAll();
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
    FollowArtistRequest invalidRequest = new FollowArtistRequest(null, ARTIST_DISCOGS_ID);

    ValidatableResponse validatableResponse = requestHandler.doPost(ContentType.JSON, invalidRequest);
    ErrorResponse errorResponse = validatableResponse.extract().as(ErrorResponse.class);

    // assert
    validatableResponse.statusCode(HttpStatus.BAD_REQUEST.value())
            .contentType(ContentType.JSON);

    assertThat(errorResponse).isNotNull();
    assertThat(errorResponse.getMessages()).hasSize(2);
  }

  @Test
  @DisplayName("DELETE should should delete the entity if it exists")
  void delete_an_existing_resource_should_return_200() {
    followArtistService.followArtist(followArtistDto);

    assertThat(followArtistService.exists(followArtistDto)).isTrue();

    FollowArtistRequest request = new FollowArtistRequest(ARTIST_NAME, ARTIST_DISCOGS_ID);
    ValidatableResponse validatableResponse = requestHandler.doDelete(ContentType.JSON, request);

    // assert
    validatableResponse.statusCode(HttpStatus.OK.value());
    assertThat(followArtistService.exists(followArtistDto)).isFalse();
  }

  @Test
  @DisplayName("DELETE should should return 404 if the entity does not exist")
  void delete_an_not_existing_resource_should_return_404() {
    assertThat(followArtistService.exists(followArtistDto)).isFalse();

    FollowArtistRequest request = new FollowArtistRequest(ARTIST_NAME, ARTIST_DISCOGS_ID);
    ValidatableResponse validatableResponse = requestHandler.doDelete(ContentType.JSON, request);

    // assert
    validatableResponse.statusCode(HttpStatus.NOT_FOUND.value());
    assertThat(followArtistService.exists(followArtistDto)).isFalse();
  }
}
