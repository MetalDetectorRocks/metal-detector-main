package com.metalr2.security.handler;

import com.metalr2.config.constants.Endpoints;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

class CustomLogoutSuccessHandlerTest implements WithAssertions {

  private LogoutSuccessHandler    logoutSuccessHandler;
  private MockHttpServletRequest  httpServletRequest;
  private MockHttpServletResponse httpServletResponse;

  @BeforeEach
  void setup() {
    logoutSuccessHandler = new CustomLogoutSuccessHandler();
    httpServletRequest   = new MockHttpServletRequest();
    httpServletResponse  = new MockHttpServletResponse();
  }

  @Test
  void locateAuthenticatedUserToFrontendHomepage() throws Exception {
    Authentication authentication = new TestingAuthenticationToken("principal", "credentials");
    logoutSuccessHandler.onLogoutSuccess(httpServletRequest, httpServletResponse, authentication);

    assertThat(httpServletResponse.getStatus()).isEqualTo(HttpStatus.MOVED_TEMPORARILY.value());
    assertThat(httpServletResponse.getRedirectedUrl()).isEqualTo(Endpoints.Guest.LOGIN + "?logout");
  }

}
