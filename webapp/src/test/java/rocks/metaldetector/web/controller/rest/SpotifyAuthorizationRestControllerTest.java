package rocks.metaldetector.web.controller.rest;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rocks.metaldetector.service.spotify.SpotifyUserAuthorizationService;
import rocks.metaldetector.support.Endpoints;
import rocks.metaldetector.web.RestAssuredMockMvcUtils;
import rocks.metaldetector.web.api.request.SpotifyAuthorizationRequest;
import rocks.metaldetector.web.api.response.SpotifyUserAuthorizationExistsResponse;
import rocks.metaldetector.web.api.response.SpotifyUserAuthorizationResponse;

import java.util.stream.Stream;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@ExtendWith(MockitoExtension.class)
class SpotifyAuthorizationRestControllerTest implements WithAssertions {

  @Mock
  private SpotifyUserAuthorizationService userAuthorizationService;

  private SpotifyAuthorizationRestController underTest;

  @BeforeEach
  void setup() {
    underTest = new SpotifyAuthorizationRestController(userAuthorizationService);
    RestAssuredMockMvc.standaloneSetup(underTest,
            springSecurity((request, response, chain) -> chain.doFilter(request, response)));
  }

  @AfterEach
  void tearDown() {
    reset(userAuthorizationService);
  }

  @Nested
  @DisplayName("Tests for authorization existence check")
  class ExistAuthorizationTest {

    private RestAssuredMockMvcUtils restAssuredMockMvcUtils;

    @BeforeEach
    void setup() {
      restAssuredMockMvcUtils = new RestAssuredMockMvcUtils(Endpoints.Rest.SPOTIFY_AUTHORIZATION);
    }

    @Test
    @DisplayName("should return 200 when GET on " + Endpoints.Rest.SPOTIFY_AUTHORIZATION)
    void should_return_200() {
      // when
      var validatableResponse = restAssuredMockMvcUtils.doGet();

      // then
      validatableResponse.statusCode(OK.value());
    }

    @Test
    @DisplayName("should call SpotifyUserAuthorizationServiceService when GET on " + Endpoints.Rest.SPOTIFY_AUTHORIZATION)
    void should_call_spotify_service() {
      // when
      restAssuredMockMvcUtils.doGet();

      // then
      verify(userAuthorizationService).exists();
    }

    @Test
    @DisplayName("should return result from SpotifyUserAuthorizationServiceService when GET on " + Endpoints.Rest.SPOTIFY_AUTHORIZATION)
    void should_return_result_from_service() {
      // given
      doReturn(true).when(userAuthorizationService).exists();

      // when
      var validatableResponse = restAssuredMockMvcUtils.doGet();

      // then
      var response = validatableResponse.extract().as(SpotifyUserAuthorizationExistsResponse.class);
      assertThat(response.exists()).isTrue();
    }
  }

  @Nested
  @DisplayName("Tests for prepare authorization")
  class PrepareAuthorizationTest {

    private RestAssuredMockMvcUtils restAssuredMockMvcUtils;

    @BeforeEach
    void setup() {
      restAssuredMockMvcUtils = new RestAssuredMockMvcUtils(Endpoints.Rest.SPOTIFY_AUTHORIZATION);
    }

    @Test
    @DisplayName("POST on " + Endpoints.Rest.SPOTIFY_AUTHORIZATION + " should return 200")
    void test_post_authorization_returns_ok() {
      // when
      var validatableResponse = restAssuredMockMvcUtils.doPost();

      // then
      validatableResponse.statusCode(OK.value());
    }

    @Test
    @DisplayName("POST on " + Endpoints.Rest.SPOTIFY_AUTHORIZATION + " should call SpotifyUserAuthorizationServiceService")
    void test_post_authorization_calls_spotify_service() {
      // when
      restAssuredMockMvcUtils.doPost();

      // then
      verify(userAuthorizationService).prepareAuthorization();
    }

    @Test
    @DisplayName("POST on " + Endpoints.Rest.SPOTIFY_AUTHORIZATION + " should return expected url")
    void test_post_authorization_returns_url() {
      // given
      var expectedUrl = "i'm an url";
      doReturn(expectedUrl).when(userAuthorizationService).prepareAuthorization();

      // when
      var validatableResponse = restAssuredMockMvcUtils.doPost();

      // then
      var response = validatableResponse.extract().as(SpotifyUserAuthorizationResponse.class);
      assertThat(response.getAuthorizationUrl()).isEqualTo(expectedUrl);
    }
  }

  @Nested
  @TestInstance(TestInstance.Lifecycle.PER_CLASS)
  @DisplayName("Tests for update authorization")
  class UpdateAuthorizationTest {

    private RestAssuredMockMvcUtils restAssuredMockMvcUtils;

    @BeforeEach
    void setup() {
      restAssuredMockMvcUtils = new RestAssuredMockMvcUtils(Endpoints.Rest.SPOTIFY_AUTHORIZATION);
    }

    @Test
    @DisplayName("PUT on " + Endpoints.Rest.SPOTIFY_AUTHORIZATION + " should return 200")
    void test_post_authorization_returns_ok() {
      // given
      var request = SpotifyAuthorizationRequest.builder().code("code").state("state").build();

      // when
      var validatableResponse = restAssuredMockMvcUtils.doPut(request);

      // then
      validatableResponse.statusCode(OK.value());
    }

    @Test
    @DisplayName("PUT on " + Endpoints.Rest.SPOTIFY_AUTHORIZATION + " should call SpotifyUserAuthorizationServiceService")
    void test_post_authorization_calls_spotify_service() {
      // given
      var request = SpotifyAuthorizationRequest.builder().code("code").state("state").build();

      // when
      restAssuredMockMvcUtils.doPut(request);

      // then
      verify(userAuthorizationService).persistInitialToken(request.getState(), request.getCode());
    }

    @ParameterizedTest
    @MethodSource("badRequestProvider")
    @DisplayName("PUT on " + Endpoints.Rest.SPOTIFY_AUTHORIZATION + " should return bad request")
    void test_post_authorization_returns_url(String code, String state) {
      // given
      var request = SpotifyAuthorizationRequest.builder().code(code).state(state).build();

      // when
      var validatableResponse = restAssuredMockMvcUtils.doPut(request);

      // then
      validatableResponse.statusCode(BAD_REQUEST.value());
    }

    private Stream<Arguments> badRequestProvider() {
      return Stream.of(
              Arguments.of((Object) null, (Object) null),
              Arguments.of("", "")
      );
    }
  }
}