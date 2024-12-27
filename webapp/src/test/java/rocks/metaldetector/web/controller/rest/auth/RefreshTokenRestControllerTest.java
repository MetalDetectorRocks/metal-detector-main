package rocks.metaldetector.web.controller.rest.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import rocks.metaldetector.service.auth.RefreshTokenData;
import rocks.metaldetector.service.auth.RefreshTokenService;
import rocks.metaldetector.service.exceptions.RestExceptionsHandler;
import rocks.metaldetector.web.api.auth.AuthenticationResponse;
import rocks.metaldetector.web.api.auth.LoginResponse;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpHeaders.SET_COOKIE;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static rocks.metaldetector.persistence.domain.user.UserRole.ROLE_USER;
import static rocks.metaldetector.service.auth.RefreshTokenService.REFRESH_TOKEN_COOKIE_NAME;
import static rocks.metaldetector.support.Endpoints.Rest.AUTHENTICATION;
import static rocks.metaldetector.support.Endpoints.Rest.REFRESH_ACCESS_TOKEN;

@ExtendWith(MockitoExtension.class)
public class RefreshTokenRestControllerTest  implements WithAssertions {

  @Mock
  private RefreshTokenService refreshTokenService;

  @InjectMocks
  private RefreshTokenRestController underTest;

  private final ObjectMapper objectMapper = new ObjectMapper();
  private MockMvc mockMvc;

  @BeforeEach
  void setup() {
    mockMvc = MockMvcBuilders.standaloneSetup(underTest).setControllerAdvice(RestExceptionsHandler.class).build();
  }

  @AfterEach
  void tearDown() {
    reset(refreshTokenService);
  }

  @Nested
  @DisplayName("Authenticated tests")
  class AuthenticatedTests {

    @Test
    @DisplayName("should return false if user is not authenticated")
    void should_return_false_if_user_is_not_authenticated() throws Exception {
      // given
      doReturn(false).when(refreshTokenService).isValid(any());

      // when
      var response = mockMvc.perform(get(AUTHENTICATION))
          .andReturn();

      // then
      var authenticationResponse = objectMapper.readValue(response.getResponse().getContentAsString(), AuthenticationResponse.class);
      assertThat(response.getResponse().getStatus()).isEqualTo(OK.value());
      assertThat(authenticationResponse.isAuthenticated()).isFalse();
    }

    @Test
    @DisplayName("should return true if user is authenticated")
    void should_return_true_if_user_is_authenticated() throws Exception {
      // given
      doReturn(true).when(refreshTokenService).isValid(any());

      // when
      var response = mockMvc.perform(get(AUTHENTICATION))
          .andReturn();

      // then
      var authenticationResponse = objectMapper.readValue(response.getResponse().getContentAsString(), AuthenticationResponse.class);
      assertThat(response.getResponse().getStatus()).isEqualTo(OK.value());
      assertThat(authenticationResponse.isAuthenticated()).isTrue();
    }

    @Test
    @DisplayName("should pass token from cookie")
    void should_pass_token_from_cookie() throws Exception {
      // given
      String token = "eyFoobar";

      // when
      mockMvc.perform(get(AUTHENTICATION)
          .cookie(new Cookie(REFRESH_TOKEN_COOKIE_NAME, token)))
          .andReturn();

      // then
      verify(refreshTokenService).isValid(token);
    }
  }

  @Nested
  @DisplayName("RefreshAccessToken tests")
  class RefreshAccessTokenTests {

    @Test
    @DisplayName("should return 401 if refresh token cookie is not present")
    void should_return_401_if_refresh_token_cookie_is_not_present() throws Exception {
      // when
      var response = mockMvc.perform(get(REFRESH_ACCESS_TOKEN))
          .andReturn()
          .getResponse();

      // then
      assertThat(response.getStatus()).isEqualTo(UNAUTHORIZED.value());
    }

    @Test
    @DisplayName("should return ok")
    void should_return_ok() throws Exception {
      // given
      var refreshTokenData = new RefreshTokenData(
          "dummy",
          List.of(ROLE_USER.getDisplayName()),
          "eyAccessToken",
          ResponseCookie.from("foo", "bar").build()
      );
      doReturn(refreshTokenData).when(refreshTokenService).refreshTokens(any());

      // when
      var response = mockMvc.perform(get(REFRESH_ACCESS_TOKEN)
          .cookie(new Cookie(REFRESH_TOKEN_COOKIE_NAME, "eyFoo")))
          .andReturn()
          .getResponse();

      // then
      assertThat(response.getStatus()).isEqualTo(OK.value());
    }

    @Test
    @DisplayName("should pass refresh token from cookie to refresh token service")
    void should_pass_refresh_token_from_cookie_to_refresh_token_service() throws Exception {
      // given
      var refreshToken = "eyRefreshToken";

      // when
      mockMvc.perform(get(REFRESH_ACCESS_TOKEN)
              .cookie(new Cookie(REFRESH_TOKEN_COOKIE_NAME, refreshToken)))
          .andReturn();

      // then
      verify(refreshTokenService).refreshTokens(refreshToken);
    }

    @Test
    @DisplayName("should return login response in body")
    void should_return_login_response() throws Exception {
      // given
      var refreshTokenData = new RefreshTokenData(
          "dummy",
          List.of(ROLE_USER.getDisplayName()),
          "eyAccessToken",
          ResponseCookie.from("foo", "bar").build()
      );
      doReturn(refreshTokenData).when(refreshTokenService).refreshTokens(any());

      // when
      var response = mockMvc.perform(get(REFRESH_ACCESS_TOKEN)
          .cookie(new Cookie(REFRESH_TOKEN_COOKIE_NAME, "eyFoo")))
          .andReturn()
          .getResponse();

      // then
      var extractedResponse = objectMapper.readValue(response.getContentAsString(), LoginResponse.class);
      assertThat(extractedResponse).isEqualTo(new LoginResponse("dummy", List.of("User"), "eyAccessToken"));
    }

    @Test
    @DisplayName("should return response with cookie header")
    void should_return_response_with_cookie_header() throws Exception {
      // given
      var refreshTokenData = new RefreshTokenData(
          "dummy",
          List.of(ROLE_USER.getDisplayName()),
          "eyAccessToken",
          ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, "eyRefreshToken").build()
      );
      doReturn(refreshTokenData).when(refreshTokenService).refreshTokens(any());

      // when
      var response = mockMvc.perform(get(REFRESH_ACCESS_TOKEN)
          .cookie(new Cookie(REFRESH_TOKEN_COOKIE_NAME, "eyFoo")))
          .andReturn()
          .getResponse();

      // then
      var headers = response.getHeaders(SET_COOKIE);
      assertThat(headers).contains("refresh_token=eyRefreshToken");
    }
  }
}
