package rocks.metaldetector.web.controller.rest;

import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import io.restassured.module.mockmvc.response.ValidatableMockMvcResponse;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import rocks.metaldetector.config.constants.Endpoints;
import rocks.metaldetector.persistence.domain.user.UserEntity;
import rocks.metaldetector.security.CurrentUserSupplier;
import rocks.metaldetector.service.artist.ArtistEntityFactory;
import rocks.metaldetector.web.RestAssuredMockMvcUtils;
import rocks.metaldetector.web.api.response.MyArtistsResponse;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@ExtendWith(MockitoExtension.class)
class MyArtistsRestControllerTest implements WithAssertions {

  private static final long DISCOGS_ID = 252211L;
  private static final String ARTIST_NAME = "252211";
  private static final int PAGE = 0;
  private static final int SIZE = 10;

  @Mock
  private CurrentUserSupplier currentUserSupplier;

  @InjectMocks
  private MyArtistsRestController underTest;

  @Mock
  private UserEntity userEntity;

  private RestAssuredMockMvcUtils restAssuredMockMvcUtils;

  @BeforeEach
  void setUp() {
    restAssuredMockMvcUtils = new RestAssuredMockMvcUtils(Endpoints.Rest.MY_ARTISTS);
    RestAssuredMockMvc.standaloneSetup(underTest,
                                       springSecurity((request, response, chain) -> chain.doFilter(request, response)));
  }

  @AfterEach
  void tearDown() {
    reset(currentUserSupplier, userEntity);
  }

  @Test
  @DisplayName("GET should return 200")
  void get_should_return_200() {
    // given
    when(currentUserSupplier.get()).thenReturn(userEntity);
    when(userEntity.getFollowedArtists()).thenReturn(Set.of(ArtistEntityFactory.withDiscogsId(DISCOGS_ID)));

    // when
    ValidatableMockMvcResponse validatableResponse = restAssuredMockMvcUtils.doGet(Map.of("page", PAGE, "size", SIZE));

    // then
    validatableResponse
        .contentType(ContentType.JSON)
        .statusCode(HttpStatus.OK.value());
  }

  @Test
  @DisplayName("GET should return results if present")
  void get_should_return_results() {
    // given
    when(currentUserSupplier.get()).thenReturn(userEntity);
    when(userEntity.getFollowedArtists()).thenReturn(Set.of(ArtistEntityFactory.withDiscogsId(DISCOGS_ID)));

    // when
    ValidatableMockMvcResponse validatableResponse = restAssuredMockMvcUtils.doGet(Map.of("page", PAGE, "size", SIZE));

    // then
    MyArtistsResponse response = validatableResponse.extract().as(MyArtistsResponse.class);

    assertThat(response).isNotNull();
    assertThat(response.getMyArtists()).hasSize(1);
    assertThat(response.getMyArtists().get(0).getDiscogsId()).isEqualTo(DISCOGS_ID);
    assertThat(response.getMyArtists().get(0).getArtistName()).isEqualTo(ARTIST_NAME);
  }

  @Test
  @DisplayName("GET should return pagination")
  void get_should_return_pagination() {
    // given
    when(currentUserSupplier.get()).thenReturn(userEntity);
    when(userEntity.getFollowedArtists()).thenReturn(Set.of(ArtistEntityFactory.withDiscogsId(DISCOGS_ID)));

    // when
    ValidatableMockMvcResponse validatableResponse = restAssuredMockMvcUtils.doGet(Map.of("page", PAGE, "size", SIZE));

    // then
    MyArtistsResponse response = validatableResponse.extract().as(MyArtistsResponse.class);

    assertThat(response.getPagination()).isNotNull();
    assertThat(response.getPagination().getTotalPages()).isEqualTo(1);
    assertThat(response.getPagination().getItemsPerPage()).isEqualTo(SIZE);
    assertThat(response.getPagination().getCurrentPage()).isEqualTo(PAGE);
  }

  @Test
  @DisplayName("GET should call current user supplier")
  void get_should_call_artists_service() {
    // given
    when(currentUserSupplier.get()).thenReturn(userEntity);
    when(userEntity.getFollowedArtists()).thenReturn(Set.of(ArtistEntityFactory.withDiscogsId(DISCOGS_ID)));

    // when
    restAssuredMockMvcUtils.doGet(Map.of("page", PAGE, "size", SIZE));

    // then
    verify(currentUserSupplier, times(1)).get();
    verify(userEntity, times(1)).getFollowedArtists();
  }

  @Test
  @DisplayName("GET should return empty list if nothing is present")
  void get_should_return_empty_list() {
    // given
    when(currentUserSupplier.get()).thenReturn(userEntity);
    when(userEntity.getFollowedArtists()).thenReturn(Collections.emptySet());

    // when
    ValidatableMockMvcResponse validatableResponse = restAssuredMockMvcUtils.doGet(Map.of("page", PAGE, "size", SIZE));

    // then
    MyArtistsResponse response = validatableResponse.extract().as(MyArtistsResponse.class);

    assertThat(response).isNotNull();
    assertThat(response.getMyArtists()).isEmpty();
  }

  @Test
  @DisplayName("GET should return empty pagination if nothing is present")
  void get_should_return_empty_pagination() {
    // given
    when(currentUserSupplier.get()).thenReturn(userEntity);
    when(userEntity.getFollowedArtists()).thenReturn(Collections.emptySet());

    // when
    ValidatableMockMvcResponse validatableResponse = restAssuredMockMvcUtils.doGet(Map.of("page", PAGE, "size", SIZE));

    // then
    MyArtistsResponse response = validatableResponse.extract().as(MyArtistsResponse.class);

    assertThat(response.getPagination()).isNotNull();
    assertThat(response.getPagination().getTotalPages()).isEqualTo(0);
    assertThat(response.getPagination().getItemsPerPage()).isEqualTo(SIZE);
    assertThat(response.getPagination().getCurrentPage()).isEqualTo(PAGE);
  }
}