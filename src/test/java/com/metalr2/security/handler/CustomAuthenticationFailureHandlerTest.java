package com.metalr2.security.handler;

import com.metalr2.config.constants.Endpoints;
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
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

class CustomAuthenticationFailureHandlerTest implements WithAssertions {

  private AuthenticationFailureHandler authenticationFailureHandler;
  private MockHttpServletRequest       httpServletRequest;
  private MockHttpServletResponse      httpServletResponse;

  @BeforeEach
  void setup() {
    authenticationFailureHandler = new CustomAuthenticationFailureHandler();
    httpServletRequest           = new MockHttpServletRequest();
    httpServletResponse          = new MockHttpServletResponse();
  }

  @Test
  @DisplayName("Forward user to login page again if the user is disabled")
  void forward_to_login_page_if_user_is_disabled() throws Exception {
    authenticationFailureHandler.onAuthenticationFailure(httpServletRequest, httpServletResponse, new DisabledException("dummy"));

    assertThat(httpServletResponse.getStatus()).isEqualTo(HttpStatus.FOUND.value());
    assertThat(httpServletResponse.getHeader(HttpHeaders.LOCATION)).isEqualTo(Endpoints.Guest.LOGIN + "?disabled");
  }

  @Test
  @DisplayName("Forward user to login page again if the user has bad credentials")
  void forward_to_login_page_if_user_has_bad_credentials() throws Exception {
    authenticationFailureHandler.onAuthenticationFailure(httpServletRequest, httpServletResponse, new BadCredentialsException("dummy"));

    assertThat(httpServletResponse.getStatus()).isEqualTo(HttpStatus.FOUND.value());
    assertThat(httpServletResponse.getRedirectedUrl()).isEqualTo(Endpoints.Guest.LOGIN + "?badCredentials");
  }

}
