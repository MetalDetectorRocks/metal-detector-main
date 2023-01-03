package rocks.metaldetector.web.controller.rest;

import io.restassured.http.Header;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import io.restassured.module.mockmvc.response.ValidatableMockMvcResponse;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseCookie;
import rocks.metaldetector.security.AuthenticationFacade;
import rocks.metaldetector.service.auth.RefreshTokenData;
import rocks.metaldetector.service.exceptions.RestExceptionsHandler;
import rocks.metaldetector.service.auth.RefreshTokenService;
import rocks.metaldetector.web.RestAssuredMockMvcUtils;
import rocks.metaldetector.web.api.auth.AuthenticationResponse;
import rocks.metaldetector.web.api.auth.LoginResponse;
import rocks.metaldetector.web.controller.rest.auth.AuthenticationRestController;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpHeaders.SET_COOKIE;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static rocks.metaldetector.persistence.domain.user.UserRole.ROLE_USER;
import static rocks.metaldetector.support.Endpoints.Rest.AUTHENTICATION;
import static rocks.metaldetector.support.Endpoints.Rest.REFRESH_ACCESS_TOKEN;

@ExtendWith(MockitoExtension.class)
class AuthenticationRestControllerTest implements WithAssertions {

  @Mock
  private AuthenticationFacade authenticationFacade;

  @Mock
  private RefreshTokenService refreshTokenService;

  @InjectMocks
  private AuthenticationRestController underTest;

  private RestAssuredMockMvcUtils restAssuredUtils;

  @AfterEach
  void tearDown() {
    reset(authenticationFacade, refreshTokenService);
  }

  @Nested
  class AuthenticatedTests {

    @BeforeEach
    void setup() {
      restAssuredUtils = new RestAssuredMockMvcUtils(AUTHENTICATION);
      RestAssuredMockMvc.standaloneSetup(underTest, RestExceptionsHandler.class);
    }

    @Test
    @DisplayName("should return false if user is not authenticated")
    void should_return_false_if_user_is_not_authenticated() {
      // given
      doReturn(false).when(authenticationFacade).isAuthenticated();

      // when
      ValidatableMockMvcResponse response = restAssuredUtils.doGet();

      // then
      var authenticationResponse = response.extract().as(AuthenticationResponse.class);
      response.status(OK);
      assertThat(authenticationResponse.isAuthenticated()).isFalse();
    }

    @Test
    @DisplayName("should return true if user is authenticated")
    void should_return_true_if_user_is_authenticated() {
      // given
      doReturn(true).when(authenticationFacade).isAuthenticated();

      // when
      ValidatableMockMvcResponse response = restAssuredUtils.doGet();

      // then
      var authenticationResponse = response.extract().as(AuthenticationResponse.class);
      response.status(OK);
      assertThat(authenticationResponse.isAuthenticated()).isTrue();
    }
  }

  @Nested
  class RefreshAccessTokenTests {

    @BeforeEach
    void setup() {
      restAssuredUtils = new RestAssuredMockMvcUtils(REFRESH_ACCESS_TOKEN);
      RestAssuredMockMvc.standaloneSetup(underTest, RestExceptionsHandler.class);
    }

    @Test
    @DisplayName("should return 401 if refresh token cookie is not present")
    void should_return_401_if_refresh_token_cookie_is_not_present() {
      // when
      var response = restAssuredUtils.doGetWithCookies(Map.of());

      // then
      response.status(UNAUTHORIZED);
    }

    @Test
    @DisplayName("should return ok")
    void should_return_ok() {
      // given
      var refreshTokenData = new RefreshTokenData(
          "dummy",
          List.of(ROLE_USER.getDisplayName()),
          "eyAccessToken",
          ResponseCookie.from("foo", "bar").build()
      );
      doReturn(refreshTokenData).when(refreshTokenService).refreshTokens(any());

      // when
      var response = restAssuredUtils.doGetWithCookies(Map.of("refresh_token", "eyFoo"));

      // then
      response.status(OK);
    }

    @Test
    @DisplayName("should pass refresh token from cookie to refresh token service")
    void should_pass_refresh_token_from_cookie_to_refresh_token_service() {
      // given
      var refreshToken = "eyRefreshToken";

      // when
      restAssuredUtils.doGetWithCookies(Map.of("refresh_token", refreshToken));

      // then
      verify(refreshTokenService).refreshTokens(refreshToken);
    }

    @Test
    @DisplayName("should return login response in body")
    void should_return_login_response() {
      // given
      var refreshTokenData = new RefreshTokenData(
          "dummy",
          List.of(ROLE_USER.getDisplayName()),
          "eyAccessToken",
          ResponseCookie.from("foo", "bar").build()
      );
      doReturn(refreshTokenData).when(refreshTokenService).refreshTokens(any());

      // when
      var response = restAssuredUtils.doGetWithCookies(Map.of("refresh_token", "eyFoo"));

      // then
      var extractedResponse = response.extract().as(LoginResponse.class);
      assertThat(extractedResponse).isEqualTo(new LoginResponse("dummy", List.of("User"), "eyAccessToken"));
    }

    @Test
    @DisplayName("should return response with cookie header")
    void should_return_response_with_cookie_header() {
      // given
      var refreshTokenData = new RefreshTokenData(
          "dummy",
          List.of(ROLE_USER.getDisplayName()),
          "eyAccessToken",
          ResponseCookie.from("refresh_token", "eyRefreshToken").build()
      );
      doReturn(refreshTokenData).when(refreshTokenService).refreshTokens(any());

      // when
      var headers = restAssuredUtils.doGetWithCookiesReturningHeaders(Map.of("refresh_token", "eyFoo"));

      // then
      assertThat(headers).contains(new Header(SET_COOKIE, "refresh_token=eyRefreshToken"));
    }
  }
}
