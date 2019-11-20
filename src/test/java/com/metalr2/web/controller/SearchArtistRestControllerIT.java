package com.metalr2.web.controller;

import com.metalr2.config.constants.Endpoints;
import com.metalr2.config.misc.DiscogsConfig;
import com.metalr2.model.user.UserEntity;
import com.metalr2.model.user.UserRepository;
import com.metalr2.model.user.UserRole;
import com.metalr2.web.DtoFactory;
import com.metalr2.web.RestAssuredRequestHandler;
import com.metalr2.web.controller.discogs.DiscogsArtistSearchRestClient;
import com.metalr2.web.controller.discogs.DiscogsArtistSearchRestClientImpl;
import com.metalr2.web.dto.discogs.search.DiscogsArtistSearchResultContainer;
import com.metalr2.web.dto.request.ArtistSearchRequest;
import com.metalr2.web.dto.response.ArtistNameSearchResponse;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

import static com.metalr2.web.controller.discogs.DiscogsArtistSearchRestClientImpl.ARTIST_NAME_SEARCH_URL_FRAGMENT;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test.properties")
@Tag("integration-test")
@ExtendWith(MockitoExtension.class)
class SearchArtistRestControllerIT implements WithAssertions {

  private static final String USERNAME                = "JohnD";
  private static final String PASSWORD                = "john.doe";
  private static final String EMAIL                   = "john.doe@example.com";
  private static final String VALID_SEARCH_REQUEST    = "Darkthrone";
  private static final String BASE_URL                = "base-url";
  private static final String SEARCH_BY_NAME_URL      = BASE_URL + ARTIST_NAME_SEARCH_URL_FRAGMENT;
  private static final int DEFAULT_PAGE = 1;
  private static final int DEFAULT_SIZE = 10;

  @Autowired
  private UserRepository userRepository;

//  @Mock
//  private DiscogsConfig discogsConfig;
//
//  @Mock
//  private RestTemplate restTemplate;
//
//  @InjectMocks
  @Mock
  private DiscogsArtistSearchRestClient artistSearchClient;

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

    DiscogsArtistSearchResultContainer resultContainer = DtoFactory.ArtistSearchResultContainerFactory.withOneResult();
//    when(discogsConfig.getRestBaseUrl()).thenReturn(BASE_URL);
//    when(restTemplate.getForEntity(SEARCH_BY_NAME_URL, DiscogsArtistSearchResultContainer.class, VALID_SEARCH_REQUEST, DEFAULT_PAGE, DEFAULT_SIZE))
//            .thenReturn(ResponseEntity.ok(resultContainer));
    when(artistSearchClient.searchByName(VALID_SEARCH_REQUEST,DEFAULT_PAGE,DEFAULT_SIZE))
            .thenReturn(Optional.of(resultContainer));

    ValidatableResponse validatableResponse = requestHandler.doPost(ContentType.JSON, artistSearchRequest);

    // assert
    validatableResponse.contentType(ContentType.JSON)
            .statusCode(HttpStatus.OK.value());

    ArtistNameSearchResponse artistNameSearchResponse = validatableResponse.extract().as(ArtistNameSearchResponse.class);
  }
}