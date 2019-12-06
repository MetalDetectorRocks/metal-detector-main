package com.metalr2.web.controller;

import com.metalr2.config.constants.Endpoints;
import com.metalr2.model.user.UserEntity;
import com.metalr2.security.CurrentUserSupplier;
import com.metalr2.service.artist.FollowArtistService;
import com.metalr2.web.RestAssuredRequestHandler;
import com.metalr2.web.dto.FollowArtistDto;
import com.metalr2.web.dto.request.FollowArtistRequest;
import com.metalr2.web.dto.response.ErrorResponse;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;

import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test.properties")
@Tag("integration-test")
@ExtendWith(MockitoExtension.class)
class FollowArtistRestControllerIT implements WithAssertions {

  private static final String ARTIST_NAME     = "Darkthrone";
  private static final long ARTIST_DISCOGS_ID = 252211L;
  private static final String USER_ID         = "TestId";

  @MockBean
  private FollowArtistService followArtistService;

  private FollowArtistRequest followArtistRequest;
  private FollowArtistDto followArtistDto = new FollowArtistDto(USER_ID, ARTIST_NAME, ARTIST_DISCOGS_ID);

  @MockBean
  private CurrentUserSupplier currentUserSupplier;

  @Mock
  private UserEntity userEntity;

  @Value("${server.address}")
  private String serverAddress;

  @LocalServerPort
  private int port;

  private RestAssuredRequestHandler<FollowArtistRequest> requestHandler;

  @BeforeEach
  void setUp() {
    String requestUri   = "http://" + serverAddress + ":" + port + Endpoints.Rest.FOLLOW_ARTISTS_V1;
    requestHandler      = new RestAssuredRequestHandler<>(requestUri);
    followArtistRequest = new FollowArtistRequest(ARTIST_NAME, ARTIST_DISCOGS_ID);
  }

  @AfterEach
  void tearDown() {
  }

  @Test
  @DisplayName("CREATE with valid request should create an entity")
  void create_with_valid_request_should_return_201() {
    // given
    doNothing().when(followArtistService).followArtist(followArtistDto);
    when(currentUserSupplier.get()).thenReturn(userEntity);
    when(userEntity.getPublicId()).thenReturn(USER_ID);

    // when
    ValidatableResponse validatableResponse = requestHandler.doPost(ContentType.JSON, followArtistRequest);

    // then
    validatableResponse.statusCode(HttpStatus.CREATED.value());
    verify(followArtistService,times(1)).followArtist(followArtistDto);
  }

  @Test
  @DisplayName("CREATE with invalid request should return 400")
  void create_with_invalid_request_should_return_400() {
    // given
    FollowArtistRequest invalidRequest = new FollowArtistRequest(null, ARTIST_DISCOGS_ID);

    // when
    ValidatableResponse validatableResponse = requestHandler.doPost(ContentType.JSON, invalidRequest);
    ErrorResponse errorResponse = validatableResponse.extract().as(ErrorResponse.class);

    // then
    validatableResponse
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .contentType(ContentType.JSON);

    assertThat(errorResponse).isNotNull();
    assertThat(errorResponse.getMessages()).hasSize(1);
  }

  @Test
  @DisplayName("DELETE should should delete the entity if it exists")
  void delete_an_existing_resource_should_return_200() {
    // given
    FollowArtistRequest request = new FollowArtistRequest(ARTIST_NAME, ARTIST_DISCOGS_ID);
    when(followArtistService.unfollowArtist(followArtistDto)).thenReturn(true);
    when(currentUserSupplier.get()).thenReturn(userEntity);
    when(userEntity.getPublicId()).thenReturn(USER_ID);

    // when
    ValidatableResponse validatableResponse = requestHandler.doDelete(ContentType.JSON, request);

    // then
    validatableResponse.statusCode(HttpStatus.OK.value());
    verify(followArtistService,times(1)).unfollowArtist(followArtistDto);
  }

  @Test
  @DisplayName("DELETE should should return 404 if the entity does not exist")
  void delete_an_not_existing_resource_should_return_404() {
    // given
    FollowArtistRequest request = new FollowArtistRequest(ARTIST_NAME, ARTIST_DISCOGS_ID);
    when(followArtistService.unfollowArtist(followArtistDto)).thenReturn(false);
    when(currentUserSupplier.get()).thenReturn(userEntity);
    when(userEntity.getPublicId()).thenReturn(USER_ID);

    // when
    ValidatableResponse validatableResponse = requestHandler.doDelete(ContentType.JSON, request);

    // then
    validatableResponse.statusCode(HttpStatus.NOT_FOUND.value());
    verify(followArtistService,times(1)).unfollowArtist(followArtistDto);
  }

}
