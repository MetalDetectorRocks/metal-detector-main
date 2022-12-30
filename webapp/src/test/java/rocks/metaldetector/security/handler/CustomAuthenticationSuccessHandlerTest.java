package rocks.metaldetector.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseCookie;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import rocks.metaldetector.persistence.domain.user.AbstractUserEntity;
import rocks.metaldetector.security.AuthenticationFacade;
import rocks.metaldetector.service.auth.RefreshTokenService;
import rocks.metaldetector.support.JwtsSupport;
import rocks.metaldetector.support.SecurityProperties;
import rocks.metaldetector.web.api.auth.LoginResponse;

import java.io.IOException;
import java.io.OutputStream;
import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpHeaders.SET_COOKIE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static rocks.metaldetector.persistence.domain.user.UserRole.ROLE_ADMINISTRATOR;

@ExtendWith(MockitoExtension.class)
class CustomAuthenticationSuccessHandlerTest implements WithAssertions {

  @InjectMocks
  private CustomAuthenticationSuccessHandler underTest;

  @Mock
  private ObjectMapper objectMapper;

  @Mock
  private AuthenticationFacade authenticationFacade;

  @Mock
  private JwtsSupport jwtsSupport;

  @Mock
  private SecurityProperties securityProperties;

  @Mock
  private RefreshTokenService refreshTokenService;

  @Mock
  private AbstractUserEntity userMock;

  private final MockHttpServletRequest  request = new MockHttpServletRequest();
  private final MockHttpServletResponse response = new MockHttpServletResponse();

  @BeforeEach
  void beforeEach() {
    doReturn(userMock).when(authenticationFacade).getCurrentUser();
    doReturn(ResponseCookie.from("foo", "bar").build()).when(refreshTokenService).createRefreshTokenCookie(any());
  }

  @Test
  @DisplayName("should generate authentication token with public user id and configured duration")
  void should_generate_authentication_token_with_public_user_id_and_configured_duration() throws IOException {
    // given
    long durationInMinutes = 123;
    doReturn(durationInMinutes).when(securityProperties).getAccessTokenExpirationInMin();

    String publicUserId = UUID.randomUUID().toString();
    doReturn(publicUserId).when(userMock).getPublicId();

    // when
    underTest.onAuthenticationSuccess(request, response, null);

    // then
    verify(jwtsSupport).generateToken(publicUserId, Duration.ofMinutes(durationInMinutes));
  }

  @Test
  @DisplayName("should create refresh token")
  void should_create_refresh_token() throws IOException {
    // given
    String username = "test-user";
    doReturn(username).when(userMock).getUsername();

    // when
    underTest.onAuthenticationSuccess(request, response, null);

    // then
    verify(refreshTokenService).createRefreshTokenCookie(username);
  }

  @Test
  @DisplayName("should set refresh token as cookie")
  void should_set_refresh_token_as_cookie() throws IOException {
    // given
    var response = mock(HttpServletResponse.class);

    // when
    underTest.onAuthenticationSuccess(request, response, null);

    // then
    verify(response).setHeader(SET_COOKIE, "foo=bar");
  }

  @Test
  @DisplayName("should set content type")
  void should_set_content_type() throws IOException {
    // given
    var response = mock(HttpServletResponse.class);

    // when
    underTest.onAuthenticationSuccess(request, response, null);

    // then
    verify(response).setContentType(APPLICATION_JSON_VALUE);
  }

  @Test
  @DisplayName("should return login response with username, token and roles")
  void should_return_login_response_with_username_token_and_roles() throws IOException {
    // given
    var username = "test-user";
    var token = "eyToken";
    doReturn(Set.of(ROLE_ADMINISTRATOR)).when(userMock).getUserRoles();
    doReturn(username).when(userMock).getUsername();
    doReturn(token).when(jwtsSupport).generateToken(any(), any());

    // when
    underTest.onAuthenticationSuccess(request, response, null);

    // then
    ArgumentCaptor<LoginResponse> captor = ArgumentCaptor.forClass(LoginResponse.class);
    verify(objectMapper).writeValue(any(OutputStream.class), captor.capture());
    assertThat(captor.getValue()).isEqualTo(new LoginResponse(
        username,
        List.of("Administrator"),
        token
    ));
  }
}
