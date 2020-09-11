package rocks.metaldetector.security.handler;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import rocks.metaldetector.support.Endpoints;

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
  @DisplayName("Forward user to login page on logout")
  void forward_user_to_login_page_on_logout() throws Exception {
    Authentication authentication = new TestingAuthenticationToken("principal", "credentials");
    logoutSuccessHandler.onLogoutSuccess(httpServletRequest, httpServletResponse, authentication);

    assertThat(httpServletResponse.getStatus()).isEqualTo(HttpStatus.FOUND.value());
    assertThat(httpServletResponse.getRedirectedUrl()).isEqualTo(Endpoints.Guest.LOGIN + "?logout");
  }

}
