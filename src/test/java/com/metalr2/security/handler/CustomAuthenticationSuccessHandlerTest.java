package com.metalr2.security.handler;

import com.metalr2.config.constants.Endpoints;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CustomAuthenticationSuccessHandlerTest {

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
  void locateAuthenticatedUserToFrontendHomepage() throws Exception {
    Authentication authentication = new TestingAuthenticationToken("principal", "credentials");
    authenticationSuccessHandler.onAuthenticationSuccess(httpServletRequest, httpServletResponse, authentication);

    assertEquals(HttpStatus.MOVED_TEMPORARILY.value(), httpServletResponse.getStatus());
    assertEquals(Endpoints.Frontend.HOME, httpServletResponse.getRedirectedUrl());
  }

}
