package rocks.metaldetector.web.controller.rest;

import io.restassured.http.Header;
import io.restassured.http.Headers;
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
import rocks.metaldetector.service.auth.TokenPair;
import rocks.metaldetector.service.exceptions.RestExceptionsHandler;
import rocks.metaldetector.service.auth.AuthService;
import rocks.metaldetector.service.auth.RefreshTokenService;
import rocks.metaldetector.web.RestAssuredMockMvcUtils;
import rocks.metaldetector.web.api.auth.AccessTokenResponse;
import rocks.metaldetector.web.api.request.LoginRequest;
import rocks.metaldetector.web.api.auth.AuthenticationResponse;
import rocks.metaldetector.web.api.auth.LoginResponse;
import rocks.metaldetector.web.controller.rest.auth.AuthenticationRestController;

import java.util.ArrayList;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpHeaders.SET_COOKIE;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;
import static rocks.metaldetector.support.Endpoints.Rest.AUTHENTICATION;
import static rocks.metaldetector.support.Endpoints.Rest.LOGIN;
import static rocks.metaldetector.support.Endpoints.Rest.REFRESH_ACCESS_TOKEN;

@ExtendWith(MockitoExtension.class)
class AuthenticationRestControllerTest implements WithAssertions {

  @Mock
  private AuthenticationFacade authenticationFacade;

  @Mock
  private AuthService authService;

  @Mock
  private RefreshTokenService refreshTokenService;

  @InjectMocks
  private AuthenticationRestController underTest;

  private RestAssuredMockMvcUtils restAssuredUtils;

  @AfterEach
  void tearDown() {
    reset(authenticationFacade, authService, refreshTokenService);
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
  class LoginUserTests {

    @BeforeEach
    void setup() {
      restAssuredUtils = new RestAssuredMockMvcUtils(LOGIN);
      RestAssuredMockMvc.standaloneSetup(underTest, RestExceptionsHandler.class);
    }

    @Test
    @DisplayName("should pass login request to auth service")
    void should_pass_login_request_to_auth_service() {
      // given
      var loginRequest = new LoginRequest("user", "pass");

      // when
      restAssuredUtils.doPost(loginRequest);

      // then
      verify(authService).loginUser(loginRequest);
    }

    @Test
    @DisplayName("should pass username to refresh token service")
    void should_pass_username_to_refresh_token_service() {
      // given
      var loginResponse = new LoginResponse("user", new ArrayList<>(), "test-token");
      doReturn(loginResponse).when(authService).loginUser(any());
      doReturn(ResponseCookie.from("foo", "bar").build()).when(refreshTokenService).createRefreshTokenCookie(any());

      // when
      restAssuredUtils.doPost(new LoginRequest("user", "pass"));

      // then
      verify(refreshTokenService).createRefreshTokenCookie("user");
    }

    @Test
    @DisplayName("should return login response in body")
    void should_return_login_response() {
      // given
      var loginResponse = new LoginResponse("user", new ArrayList<>(), "test-token");
      doReturn(loginResponse).when(authService).loginUser(any());
      doReturn(ResponseCookie.from("foo", "bar").build()).when(refreshTokenService).createRefreshTokenCookie(any());

      // when
      ValidatableMockMvcResponse response = restAssuredUtils.doPost(new LoginRequest("user", "pass"));

      // then
      var extractedResponse = response.extract().as(LoginResponse.class);
      response.status(OK);
      assertThat(extractedResponse).isEqualTo(loginResponse);
    }

    @Test
    @DisplayName("should return response with cookie header")
    void should_return_response_with_cookie_header() {
      // given
      doReturn(new LoginResponse("user", new ArrayList<>(), "test-token")).when(authService).loginUser(any());
      doReturn(ResponseCookie.from("foo", "bar").build()).when(refreshTokenService).createRefreshTokenCookie(any());

      // when
      Headers headers = restAssuredUtils.doPostReturningHeaders(new LoginRequest("user", "pass"));

      // then
      assertThat(headers).contains(new Header(SET_COOKIE, "foo=bar"));
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
    @DisplayName("should return 400 if refresh token cookie is not present")
    void should_return_400_if_refresh_token_cookie_is_not_present() {
      // when
      var response = restAssuredUtils.doGetWithCookies(Map.of());

      // then
      response.status(BAD_REQUEST);
    }

    @Test
    @DisplayName("should return ok")
    void should_return_ok() {
      // given
      var tokenPair = new TokenPair("eyAccessToken", ResponseCookie.from("foo", "bar").build());
      doReturn(tokenPair).when(refreshTokenService).refreshTokens(any());

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
    @DisplayName("should return access token response in body")
    void should_return_access_token_response() {
      // given
      var accessTokenResponse = new AccessTokenResponse("eyAccessToken");
      var tokenPair = new TokenPair("eyAccessToken", ResponseCookie.from("foo", "bar").build());
      doReturn(tokenPair).when(refreshTokenService).refreshTokens(any());

      // when
      var response = restAssuredUtils.doGetWithCookies(Map.of("refresh_token", "eyFoo"));

      // then
      var extractedResponse = response.extract().as(AccessTokenResponse.class);
      assertThat(extractedResponse).isEqualTo(accessTokenResponse);
    }

    @Test
    @DisplayName("should return response with cookie header")
    void should_return_response_with_cookie_header() {
      // given
      var tokenPair = new TokenPair("eyAccessToken", ResponseCookie.from("refresh_token", "eyRefreshToken").build());
      doReturn(tokenPair).when(refreshTokenService).refreshTokens(any());

      // when
      var headers = restAssuredUtils.doGetWithCookiesReturningHeaders(Map.of("refresh_token", "eyFoo"));

      // then
      assertThat(headers).contains(new Header(SET_COOKIE, "refresh_token=eyRefreshToken"));
    }
  }
}
