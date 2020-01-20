package com.metalr2.security.handler;

import com.metalr2.config.constants.Endpoints;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

class CustomAuthenticationSuccessHandlerTest implements WithAssertions {

  private AuthenticationSuccessHandler authenticationSuccessHandler;
  private MockHttpServletRequest       httpServletRequest;
  private MockHttpServletResponse      httpServletResponse;

  @BeforeEach
  void setup() {
    authenticationSuccessHandler = new CustomAuthenticationSuccessHandler();
    httpServletRequest           = new MockHttpServletRequest();
    httpServletResponse          = new MockHttpServletResponse();
  }

  @Test
  @DisplayName("Forward user to frontend home on successful authentication")
  void forward_user_to_frontend_homepage_on_authentication_success() throws Exception {
    Authentication authentication = new TestingAuthenticationToken("principal", "credentials");
    authenticationSuccessHandler.onAuthenticationSuccess(httpServletRequest, httpServletResponse, authentication);

    assertThat(httpServletResponse.getStatus()).isEqualTo(HttpStatus.FOUND.value());
    assertThat(httpServletResponse.getRedirectedUrl()).isEqualTo(Endpoints.Frontend.HOME);
  }

}
