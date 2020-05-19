package rocks.metaldetector.security.handler;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import rocks.metaldetector.config.constants.Endpoints;

class CustomAuthenticationFailureHandlerTest implements WithAssertions {

  private AuthenticationFailureHandler underTest;
  private MockHttpServletRequest httpServletRequest;
  private MockHttpServletResponse httpServletResponse;

  @BeforeEach
  void setup() {
    underTest = new CustomAuthenticationFailureHandler();
    httpServletRequest = new MockHttpServletRequest();
    httpServletResponse = new MockHttpServletResponse();
  }

  @Test
  @DisplayName("Forward user to login page again if the user is disabled")
  void forward_to_login_page_if_user_is_disabled() throws Exception {
    // when
    underTest.onAuthenticationFailure(httpServletRequest, httpServletResponse, new DisabledException("dummy"));

    // then
    assertThat(httpServletResponse.getStatus()).isEqualTo(HttpStatus.FOUND.value());
    assertThat(httpServletResponse.getHeader(HttpHeaders.LOCATION)).isEqualTo(Endpoints.Guest.LOGIN + "?disabled");
  }

  @Test
  @DisplayName("Forward user to login page again if the user is blocked")
  void forward_to_login_page_if_user_is_blocked() throws Exception {
    // when
    underTest.onAuthenticationFailure(httpServletRequest, httpServletResponse, new InternalAuthenticationServiceException("dummy"));

    // then
    assertThat(httpServletResponse.getStatus()).isEqualTo(HttpStatus.FOUND.value());
    assertThat(httpServletResponse.getHeader(HttpHeaders.LOCATION)).isEqualTo(Endpoints.Guest.LOGIN + "?blocked");
  }

  @Test
  @DisplayName("Forward user to login page again if the user has bad credentials")
  void forward_to_login_page_if_user_has_bad_credentials() throws Exception {
    // when
    underTest.onAuthenticationFailure(httpServletRequest, httpServletResponse, new BadCredentialsException("dummy"));

    // then
    assertThat(httpServletResponse.getStatus()).isEqualTo(HttpStatus.FOUND.value());
    assertThat(httpServletResponse.getRedirectedUrl()).isEqualTo(Endpoints.Guest.LOGIN + "?badCredentials");
  }
}
