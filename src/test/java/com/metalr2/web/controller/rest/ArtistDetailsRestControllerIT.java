package com.metalr2.web.controller.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.metalr2.config.constants.Endpoints;
import com.metalr2.model.user.UserEntity;
import com.metalr2.security.CurrentUserSupplier;
import com.metalr2.service.discogs.DiscogsArtistSearchRestClientImpl;
import com.metalr2.testutil.WithIntegrationTestProfile;
import com.metalr2.web.DtoFactory;
import com.metalr2.web.RestAssuredRequestHandler;
import com.metalr2.web.dto.request.ArtistDetailsRequest;
import com.metalr2.web.dto.response.ArtistDetailsResponse;
import com.metalr2.web.dto.response.ErrorResponse;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import java.util.Map;
import java.util.Optional;

import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(MockitoExtension.class)
class ArtistDetailsRestControllerIT implements WithAssertions, WithIntegrationTestProfile {

  private static final String USER_ID = "TestId";

  @MockBean
  private DiscogsArtistSearchRestClientImpl artistSearchClient;

  @MockBean
  private CurrentUserSupplier currentUserSupplier;

  @Mock
  private UserEntity userEntity;

  @LocalServerPort
  private int port;

  private RestAssuredRequestHandler<ArtistDetailsRequest> requestHandler;
  private ObjectMapper mapper;

  @BeforeEach
  void setUp() {
    String requestUri = "http://localhost:" + port + Endpoints.Rest.ARTIST_DETAILS_V1;
    requestHandler    = new RestAssuredRequestHandler<>(requestUri);
    mapper              = new ObjectMapper();
  }

  @AfterEach
  void tearDown() {
  }

  @Test
  @DisplayName("GET with valid request should return 200")
  void get_with_valid_request_should_return_200() {
    // given
    ArtistDetailsRequest request      = new ArtistDetailsRequest("Testname",1L);
    Map<String,Object> requestParams  = mapper.convertValue(request,new TypeReference<Map<String, Object>>() {});
    when(artistSearchClient.searchById(request.getArtistId())).thenReturn(Optional.of(DtoFactory.ArtistFactory.createTestArtist()));
    when(currentUserSupplier.get()).thenReturn(userEntity);
    when(userEntity.getPublicId()).thenReturn(USER_ID);

    // when
    ValidatableResponse validatableResponse = requestHandler.doGet(ContentType.JSON, requestParams);

    // then
    validatableResponse
            .contentType(ContentType.JSON)
            .statusCode(HttpStatus.OK.value());

    ArtistDetailsResponse response = validatableResponse.extract().as(ArtistDetailsResponse.class);
    assertThat(response).isNotNull();
    assertThat(response.getArtistId()).isEqualTo(1L);
    verify(artistSearchClient,times(1)).searchById(request.getArtistId());
  }

  @Test
  @DisplayName("GET with bad request should return 400")
  void get_with_bad_request_should_return_400() {
    // given
    ArtistDetailsRequest request      = new ArtistDetailsRequest(null,null);
    Map<String,Object> requestParams  = mapper.convertValue(request,new TypeReference<Map<String, Object>>() {});

    // when
    ValidatableResponse validatableResponse = requestHandler.doGet(ContentType.JSON, requestParams);
    ErrorResponse errorResponse = validatableResponse.extract().as(ErrorResponse.class);

    // then
    validatableResponse
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .contentType(ContentType.JSON);

    assertThat(errorResponse).isNotNull();
    assertThat(errorResponse.getMessages()).hasSize(2);
  }

  @Test
  @DisplayName("GET with no results should return 404")
  void get_with_no_results_should_return_404() {
    // given
    ArtistDetailsRequest request      = new ArtistDetailsRequest("Testname",0L);
    Map<String,Object> requestParams  = mapper.convertValue(request,new TypeReference<Map<String, Object>>() {});
    when(artistSearchClient.searchById(request.getArtistId())).thenReturn(Optional.empty());

    // when
    ValidatableResponse validatableResponse = requestHandler.doGet(ContentType.JSON, requestParams);

    // then
    validatableResponse.statusCode(HttpStatus.NOT_FOUND.value());
    verify(artistSearchClient,times(1)).searchById(request.getArtistId());
  }

}
