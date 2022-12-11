package rocks.metaldetector.service.auth;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import rocks.metaldetector.persistence.domain.user.AbstractUserEntity;
import rocks.metaldetector.security.AuthenticationFacade;
import rocks.metaldetector.support.JwtsSupport;
import rocks.metaldetector.support.SecurityProperties;
import rocks.metaldetector.web.api.request.LoginRequest;
import rocks.metaldetector.web.api.auth.LoginResponse;

import javax.servlet.http.HttpServletRequest;

import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static rocks.metaldetector.persistence.domain.user.UserRole.ROLE_ADMINISTRATOR;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest implements WithAssertions {

  @Mock
  private JwtsSupport jwtsSupport;

  @Mock
  private AuthenticationFacade authenticationFacade;

  @Mock
  private AuthenticationManager authenticationManager;

  @Mock
  private AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource;

  @Mock
  private HttpServletRequest httpRequest;

  @Mock
  private SecurityProperties securityProperties;

  @Mock
  private AbstractUserEntity userMock;

  @InjectMocks
  private AuthService underTest;

  @AfterEach
  void afterEach() {
    reset(jwtsSupport, authenticationFacade, authenticationManager, authenticationDetailsSource, httpRequest, securityProperties, userMock);
  }

  @Nested
  class LoginUserTests {

    @BeforeEach
    void beforeEach() {
      doReturn(userMock).when(authenticationFacade).getCurrentUser();
      doReturn(Set.of(ROLE_ADMINISTRATOR)).when(userMock).getUserRoles();
    }

    @Test
    @DisplayName("should authenticate with username and password via authentication manager")
    void should_authenticate_user_via_authentication_manager() {
      // given
      ArgumentCaptor<UsernamePasswordAuthenticationToken> authTokenCaptor = ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);
      var loginRequest = new LoginRequest("user", "pass");

      // when
      underTest.loginUser(loginRequest);

      // then
      verify(authenticationManager).authenticate(authTokenCaptor.capture());
      assertThat(authTokenCaptor.getValue().getPrincipal()).isEqualTo(loginRequest.getUsername());
      assertThat(authTokenCaptor.getValue().getCredentials()).isEqualTo(loginRequest.getPassword());
    }

    @Test
    @DisplayName("should create authentication details from http request")
    void should_create_authentication_details_from_http_request() {
      // given
      var details = "test-details";
      doReturn(details).when(authenticationDetailsSource).buildDetails(any());
      ArgumentCaptor<UsernamePasswordAuthenticationToken> authTokenCaptor = ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);

      // when
      underTest.loginUser(new LoginRequest("user", "pass"));

      // then
      verify(authenticationDetailsSource).buildDetails(httpRequest);
      verify(authenticationManager).authenticate(authTokenCaptor.capture());
      assertThat(authTokenCaptor.getValue().getDetails()).isEqualTo(details);
    }

    @Test
    @DisplayName("should generate authentication token with public user id and configured duration")
    void should_generate_authentication_token_with_public_user_id_and_configured_duration() {
      // given
      long durationInMinutes = 123;
      doReturn(durationInMinutes).when(securityProperties).getAccessTokenExpirationInMin();

      String publicUserId = UUID.randomUUID().toString();
      doReturn(publicUserId).when(userMock).getPublicId();

      // when
      underTest.loginUser(new LoginRequest("user", "pass"));

      // then
      verify(jwtsSupport).generateToken(eq(publicUserId), eq(Duration.ofMinutes(durationInMinutes)));
    }

    @Test
    @DisplayName("should return login response with username, token and roles")
    void should_return_login_response_with_username_token_and_roles() {
      // given
      var loginRequest = new LoginRequest("user", "pass");

      var token = "test-token";
      doReturn(token).when(jwtsSupport).generateToken(any(), any());

      // when
      var result = underTest.loginUser(loginRequest);

      // then
      assertThat(result).isEqualTo(new LoginResponse(
          loginRequest.getUsername(),
          List.of("Administrator"),
          token
      ));
    }
  }
}
