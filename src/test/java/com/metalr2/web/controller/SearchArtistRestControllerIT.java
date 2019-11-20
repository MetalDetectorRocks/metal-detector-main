package com.metalr2.web.controller;

import com.metalr2.config.constants.Endpoints;
import com.metalr2.model.user.UserEntity;
import com.metalr2.model.user.UserRepository;
import com.metalr2.model.user.UserRole;
import com.metalr2.web.DtoFactory;
import com.metalr2.web.RestAssuredRequestHandler;
import com.metalr2.web.controller.discogs.DiscogsArtistSearchRestClientImpl;
import com.metalr2.web.dto.request.ArtistSearchRequest;
import com.metalr2.web.dto.response.ArtistNameSearchResponse;
import com.metalr2.web.dto.response.Pagination;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;

import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test.properties")
@Tag("integration-test")
@ExtendWith(MockitoExtension.class)
class SearchArtistRestControllerIT implements WithAssertions {

  private static final String USERNAME                = "JohnD";
  private static final String PASSWORD                = "john.doe";
  private static final String EMAIL                   = "john.doe@example.com";
  private static final String VALID_SEARCH_REQUEST    = "Darkthrone";
  private static final long DISCOGS_ARTIST_ID         = 252211L;
  private static final int DEFAULT_PAGE = 1;
  private static final int DEFAULT_SIZE = 10;

  @Autowired
  private UserRepository userRepository;

  @MockBean
  private DiscogsArtistSearchRestClientImpl artistSearchClient;

  @Value("${server.address}")
  private String serverAddress;

  @LocalServerPort
  private int port;

  private RestAssuredRequestHandler<ArtistSearchRequest> requestHandler;

  @BeforeEach
  void setUp() {
    String requestUri   = "http://" + serverAddress + ":" + port + Endpoints.Rest.ARTISTS;
    requestHandler      = new RestAssuredRequestHandler<>(requestUri, USERNAME, PASSWORD);
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
  @DisplayName("POST with valid request should return 200")
  void post_with_valid_request_should_return_200() {
    ArtistSearchRequest artistSearchRequest = new ArtistSearchRequest(VALID_SEARCH_REQUEST,DEFAULT_PAGE,DEFAULT_SIZE);

    when(artistSearchClient.searchByName(artistSearchRequest.getArtistName(),artistSearchRequest.getPage(),artistSearchRequest.getSize()))
            .thenReturn(Optional.of(DtoFactory.ArtistSearchResultContainerFactory.withOneCertainResult()));

    ValidatableResponse validatableResponse = requestHandler.doPost(ContentType.JSON, artistSearchRequest);

    // assert
    validatableResponse.contentType(ContentType.JSON)
            .statusCode(HttpStatus.OK.value());

    ArtistNameSearchResponse artistNameSearchResponse = validatableResponse.extract().as(ArtistNameSearchResponse.class);

    assertThat(artistNameSearchResponse).isNotNull();
    assertThat(artistNameSearchResponse.getPagination()).isNotNull();

    assertThat(artistNameSearchResponse.getArtistSearchResults()).isNotNull().hasSize(1);

    ArtistNameSearchResponse.ArtistSearchResult artistSearchResult = artistNameSearchResponse.getArtistSearchResults().get(0);

    assertThat(artistSearchResult).isNotNull();
    assertThat(artistSearchResult.getArtistName()).isEqualTo(VALID_SEARCH_REQUEST);
    assertThat(artistSearchResult.getId()).isEqualTo(DISCOGS_ARTIST_ID);

    Pagination pagination = artistNameSearchResponse.getPagination();

    assertThat(pagination.getCurrentPage()).isEqualTo(DEFAULT_PAGE);
    assertThat(pagination.getSize()).isEqualTo(DEFAULT_SIZE);
    assertThat(pagination.getTotalPages()).isEqualTo(DEFAULT_PAGE+1);
    assertThat(pagination.getNextPage()).isEqualTo(DEFAULT_PAGE+1);
  }

  @Test
  @DisplayName("POST with bad request should return 400")
  void post_with_bad_request_should_return_400() {
    ArtistSearchRequest badArtistSearchRequest = new ArtistSearchRequest(null,DEFAULT_PAGE,DEFAULT_SIZE);

    ValidatableResponse validatableResponse = requestHandler.doPost(ContentType.JSON, badArtistSearchRequest);

    // assert
    validatableResponse.statusCode(HttpStatus.BAD_REQUEST.value());
  }

  @Test
  @DisplayName("POST with empty request should return 404")
  void post_with_empty_request_should_return_404() {
    ArtistSearchRequest artistSearchRequest = new ArtistSearchRequest("",DEFAULT_PAGE,DEFAULT_SIZE);
    when(artistSearchClient.searchByName(artistSearchRequest.getArtistName(),artistSearchRequest.getPage(),artistSearchRequest.getSize()))
            .thenReturn(Optional.empty());

    ValidatableResponse validatableResponse = requestHandler.doPost(ContentType.JSON, artistSearchRequest);

    // assert
    validatableResponse.statusCode(HttpStatus.NOT_FOUND.value());
  }
}
