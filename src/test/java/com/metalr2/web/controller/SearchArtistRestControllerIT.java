package com.metalr2.web.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.metalr2.config.constants.Endpoints;
import com.metalr2.model.user.UserEntity;
import com.metalr2.security.CurrentUserSupplier;
import com.metalr2.service.discogs.DiscogsArtistSearchRestClientImpl;
import com.metalr2.web.DtoFactory.ArtistSearchResultContainerFactory;
import com.metalr2.web.RestAssuredRequestHandler;
import com.metalr2.web.dto.request.ArtistSearchRequest;
import com.metalr2.web.dto.response.ArtistNameSearchResponse;
import com.metalr2.web.dto.response.ArtistNameSearchResponse.ArtistSearchResult;
import com.metalr2.web.dto.response.Pagination;
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

import java.util.Map;
import java.util.Optional;

import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test.properties")
@Tag("integration-test")
@ExtendWith(MockitoExtension.class)
class SearchArtistRestControllerIT implements WithAssertions {

  private static final String VALID_SEARCH_REQUEST      = "Darkthrone";
  private static final String NO_RESULT_SEARCH_REQUEST  = "NoResult";
  private static final long DISCOGS_ARTIST_ID           = 252211L;
  private static final String USER_ID                   = "TestId";
  private static final int DEFAULT_PAGE                 = 1;
  private static final int DEFAULT_SIZE                 = 10;
  private static final int TOTAL_PAGES                  = 2;

  @MockBean
  private DiscogsArtistSearchRestClientImpl artistSearchClient;

  @MockBean
  private CurrentUserSupplier currentUserSupplier;

  @Mock
  private UserEntity userEntity;

  @Value("${server.address}")
  private String serverAddress;

  @LocalServerPort
  private int port;

  private RestAssuredRequestHandler<ArtistSearchRequest> requestHandler;
  private ObjectMapper mapper;

  @BeforeEach
  void setUp() {
    String requestUri   = "http://" + serverAddress + ":" + port + Endpoints.Rest.ARTISTS_V1;
    requestHandler      = new RestAssuredRequestHandler<>(requestUri);
    mapper              = new ObjectMapper();
  }

  @AfterEach
  void tearDown() {
  }

  @Test
  @DisplayName("GET with valid request should return 200")
  void get_with_valid_request_should_return_200() {
    // given
    ArtistSearchRequest request = new ArtistSearchRequest(VALID_SEARCH_REQUEST,DEFAULT_PAGE,DEFAULT_SIZE);
    Map<String,Object> requestParams = mapper.convertValue(request,new TypeReference<Map<String, Object>>() {});
    when(artistSearchClient.searchByName(request.getArtistName(),request.getPage(),request.getSize()))
            .thenReturn(Optional.of(ArtistSearchResultContainerFactory.withOneCertainResult()));
    when(currentUserSupplier.get()).thenReturn(userEntity);
    when(userEntity.getPublicId()).thenReturn(USER_ID);

    // when
    ValidatableResponse validatableResponse = requestHandler.doGet(ContentType.JSON, requestParams);

    // then
    validatableResponse
            .contentType(ContentType.JSON)
            .statusCode(HttpStatus.OK.value());

    ArtistNameSearchResponse artistNameSearchResponse = validatableResponse.extract().as(ArtistNameSearchResponse.class);
    assertThat(artistNameSearchResponse.getArtistSearchResults()).isNotNull().hasSize(1);

    ArtistSearchResult artistSearchResult = artistNameSearchResponse.getArtistSearchResults().get(0);
    assertThat(artistSearchResult).isEqualTo(new ArtistSearchResult(null,DISCOGS_ARTIST_ID, VALID_SEARCH_REQUEST,false));

    Pagination pagination = artistNameSearchResponse.getPagination();
    assertThat(pagination).isEqualTo(new Pagination(TOTAL_PAGES, DEFAULT_PAGE, DEFAULT_SIZE));

    verify(artistSearchClient,times(1)).searchByName(VALID_SEARCH_REQUEST,DEFAULT_PAGE,DEFAULT_SIZE);
  }

  @Test
  @DisplayName("GET with bad request should return 400")
  void get_with_bad_request_should_return_400() {
    // given
    ArtistSearchRequest request = new ArtistSearchRequest(null,DEFAULT_PAGE,DEFAULT_SIZE);
    Map<String,Object> requestParams = mapper.convertValue(request,new TypeReference<Map<String, Object>>() {});

    // when
    ValidatableResponse validatableResponse = requestHandler.doGet(ContentType.JSON, requestParams);

    // then
    validatableResponse.statusCode(HttpStatus.BAD_REQUEST.value());
    verify(artistSearchClient,times(0)).searchByName(null,request.getPage(),request.getSize());
  }

  @Test
  @DisplayName("GET with empty result should return 404")
  void get_with_empty_result_should_return_404() {
    // given
    ArtistSearchRequest request       = new ArtistSearchRequest(NO_RESULT_SEARCH_REQUEST,DEFAULT_PAGE,DEFAULT_SIZE);
    Map<String,Object> requestParams  = mapper.convertValue(request,new TypeReference<Map<String, Object>>() {});
    when(artistSearchClient.searchByName(request.getArtistName(),request.getPage(),request.getSize()))
            .thenReturn(Optional.empty());

    // when
    ValidatableResponse validatableResponse = requestHandler.doGet(ContentType.JSON, requestParams);

    // then
    validatableResponse.statusCode(HttpStatus.NOT_FOUND.value());
    verify(artistSearchClient,times(1)).searchByName(request.getArtistName(),request.getPage(),request.getSize());
  }

}
