package rocks.metaldetector.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.Authentication;
import rocks.metaldetector.web.api.request.LoginRequest;

import java.io.IOException;
import java.io.InputStream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CustomUsernamePasswordAuthenticationFilterTest implements WithAssertions {

  @Mock
  private ObjectMapper objectMapper;

  @Mock
  @SuppressWarnings("unused")
  private AuthenticationConfiguration authenticationConfiguration;

  @Mock
  private AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource;

  @Mock
  private AuthenticationManager authenticationManager;

  @InjectMocks
  private CustomUsernamePasswordAuthenticationFilter underTest;

  private final MockHttpServletRequest  request = new MockHttpServletRequest();
  private final MockHttpServletResponse response = new MockHttpServletResponse();
  private final Authentication authentication = new TestingAuthenticationToken("principal", "credentials");

  @BeforeEach
  void beforeEach() {
    underTest.setAuthenticationManager(authenticationManager);
    underTest.setAuthenticationDetailsSource(authenticationDetailsSource);
  }

  @Test
  @DisplayName("should parse input stream into login request")
  void should_parse_input_stream_into_login_request() throws IOException {
    // given
    doReturn(new LoginRequest("user", "pass")).when(objectMapper).readValue(any(InputStream.class), eq(LoginRequest.class));

    // when
    underTest.attemptAuthentication(request, response);

    // then
    verify(objectMapper).readValue(request.getInputStream(), LoginRequest.class);
  }

  @Test
  @DisplayName("should authenticate with username and password via authentication manager")
  void should_authenticate_user_via_authentication_manager() throws IOException {
    // given
    var loginRequest = new LoginRequest("user", "pass");
    doReturn(loginRequest).when(objectMapper).readValue(any(InputStream.class), eq(LoginRequest.class));

    // when
    underTest.attemptAuthentication(request, response);

    // then
    ArgumentCaptor<UsernamePasswordAuthenticationToken> authTokenCaptor = ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);
    verify(authenticationManager).authenticate(authTokenCaptor.capture());
    assertThat(authTokenCaptor.getValue().getPrincipal()).isEqualTo(loginRequest.getUsername());
    assertThat(authTokenCaptor.getValue().getCredentials()).isEqualTo(loginRequest.getPassword());
  }

  @Test
  @DisplayName("should create authentication details from http request")
  void should_create_authentication_details_from_http_request() throws IOException {
    // given
    var details = "test-details";
    doReturn(details).when(authenticationDetailsSource).buildDetails(any());
    var loginRequest = new LoginRequest("user", "pass");
    doReturn(loginRequest).when(objectMapper).readValue(any(InputStream.class), eq(LoginRequest.class));

    // when
    underTest.attemptAuthentication(request, response);

    // then
    ArgumentCaptor<UsernamePasswordAuthenticationToken> authTokenCaptor = ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);
    verify(authenticationDetailsSource).buildDetails(request);
    verify(authenticationManager).authenticate(authTokenCaptor.capture());
    assertThat(authTokenCaptor.getValue().getDetails()).isEqualTo(details);
  }

  @Test
  @DisplayName("should return created authentication")
  void should_return_created_authentication() throws IOException {
    // given
    var loginRequest = new LoginRequest("user", "pass");
    doReturn(loginRequest).when(objectMapper).readValue(any(InputStream.class), eq(LoginRequest.class));
    doReturn(authentication).when(authenticationManager).authenticate(any());

    // when
    Authentication result = underTest.attemptAuthentication(request, response);

    // then
    assertThat(result).isEqualTo(authentication);
  }
}
