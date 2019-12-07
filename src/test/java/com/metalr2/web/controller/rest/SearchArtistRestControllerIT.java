package com.metalr2.web.controller.rest;

import com.metalr2.config.constants.Endpoints;
import com.metalr2.model.user.UserEntity;
import com.metalr2.security.CurrentUserSupplier;
import com.metalr2.service.discogs.DiscogsArtistSearchRestClientImpl;
import com.metalr2.testutil.WithIntegrationTestProfile;
import com.metalr2.web.DtoFactory.ArtistSearchResultContainerFactory;
import com.metalr2.web.RestAssuredRequestHandler;
import com.metalr2.web.dto.request.ArtistSearchRequest;
import com.metalr2.web.dto.response.ArtistNameSearchResponse;
import com.metalr2.web.dto.response.ArtistNameSearchResponse.ArtistSearchResult;
import com.metalr2.web.dto.response.Pagination;
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

import java.util.Optional;

import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(MockitoExtension.class)
class SearchArtistRestControllerIT implements WithAssertions, WithIntegrationTestProfile {

  private static final String VALID_SEARCH_REQUEST = "Darkthrone";
  private static final long DISCOGS_ARTIST_ID      = 252211L;
  private static final String USER_ID              = "TestId";
  private static final int DEFAULT_PAGE            = 1;
  private static final int DEFAULT_SIZE            = 10;
  private static final int TOTAL_PAGES             = 2;

  @MockBean
  private DiscogsArtistSearchRestClientImpl artistSearchClient;

  @MockBean
  private CurrentUserSupplier currentUserSupplier;

  @Mock
  private UserEntity userEntity;

  @LocalServerPort
  private int port;

  private RestAssuredRequestHandler<ArtistSearchRequest> requestHandler;

  @BeforeEach
  void setUp() {
    String requestUri = "http://localhost:" + port + Endpoints.Rest.ARTISTS_V1;
    requestHandler    = new RestAssuredRequestHandler<>(requestUri);
  }

  @AfterEach
  void tearDown() {
  }

  @Test
  @DisplayName("POST with valid request should return 200")
  void post_with_valid_request_should_return_200() {
    // given
    ArtistSearchRequest artistSearchRequest = new ArtistSearchRequest(VALID_SEARCH_REQUEST,DEFAULT_PAGE,DEFAULT_SIZE);
    when(artistSearchClient.searchByName(artistSearchRequest.getArtistName(),artistSearchRequest.getPage(),artistSearchRequest.getSize()))
            .thenReturn(Optional.of(ArtistSearchResultContainerFactory.withOneCertainResult()));
    when(currentUserSupplier.get()).thenReturn(userEntity);
    when(userEntity.getPublicId()).thenReturn(USER_ID);

    // when
    ValidatableResponse validatableResponse = requestHandler.doPost(ContentType.JSON, artistSearchRequest);

    // then
    validatableResponse
            .contentType(ContentType.JSON)
            .statusCode(HttpStatus.OK.value());

    ArtistNameSearchResponse artistNameSearchResponse = validatableResponse.extract().as(ArtistNameSearchResponse.class);
    assertThat(artistNameSearchResponse.getArtistSearchResults()).isNotNull().hasSize(1);

    ArtistSearchResult artistSearchResult = artistNameSearchResponse.getArtistSearchResults().get(0);
    assertThat(artistSearchResult).isEqualTo(new ArtistSearchResult(null,DISCOGS_ARTIST_ID,VALID_SEARCH_REQUEST,false));

    Pagination pagination = artistNameSearchResponse.getPagination();
    assertThat(pagination).isEqualTo(new Pagination(TOTAL_PAGES, DEFAULT_PAGE, DEFAULT_SIZE));

    verify(artistSearchClient,times(1)).searchByName(artistSearchRequest.getArtistName(),artistSearchRequest.getPage(),artistSearchRequest.getSize());
  }

  @Test
  @DisplayName("POST with bad request should return 400")
  void post_with_bad_request_should_return_400() {
    // given
    ArtistSearchRequest badArtistSearchRequest = new ArtistSearchRequest(null,DEFAULT_PAGE,DEFAULT_SIZE);

    // when
    ValidatableResponse validatableResponse = requestHandler.doPost(ContentType.JSON, badArtistSearchRequest);

    // then
    validatableResponse.statusCode(HttpStatus.BAD_REQUEST.value());
    verify(artistSearchClient,times(0)).searchByName(badArtistSearchRequest.getArtistName(),badArtistSearchRequest.getPage(),badArtistSearchRequest.getSize());
  }

  @Test
  @DisplayName("POST with empty request should return 404")
  void post_with_empty_request_should_return_404() {
    // given
    ArtistSearchRequest emptyArtistSearchRequest = new ArtistSearchRequest("",DEFAULT_PAGE,DEFAULT_SIZE);
    when(artistSearchClient.searchByName(emptyArtistSearchRequest.getArtistName(),emptyArtistSearchRequest.getPage(),emptyArtistSearchRequest.getSize()))
            .thenReturn(Optional.empty());

    // when
    ValidatableResponse validatableResponse = requestHandler.doPost(ContentType.JSON, emptyArtistSearchRequest);

    // then
    validatableResponse.statusCode(HttpStatus.NOT_FOUND.value());
    verify(artistSearchClient,times(1)).searchByName(emptyArtistSearchRequest.getArtistName(),emptyArtistSearchRequest.getPage(),emptyArtistSearchRequest.getSize());
  }

}
