package rocks.metaldetector.security.handler;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.DefaultSavedRequest;
import rocks.metaldetector.support.Endpoints;

import static org.mockito.Mockito.verify;
import static rocks.metaldetector.security.handler.CustomAuthenticationSuccessHandler.SAVED_REQUEST_ATTRIBUTE;

@ExtendWith(MockitoExtension.class)
class CustomAuthenticationSuccessHandlerTest implements WithAssertions {

  @Mock
  private SavedRequestAwareAuthenticationSuccessHandler savedRequestRedirectionHandler;

  @InjectMocks
  private CustomAuthenticationSuccessHandler underTest;

  private MockHttpServletRequest  httpServletRequest  = new MockHttpServletRequest();
  private MockHttpServletResponse httpServletResponse = new MockHttpServletResponse();
  private Authentication          authentication      = new TestingAuthenticationToken("principal", "credentials");

  @Test
  @DisplayName("Forward user to frontend home on successful authentication")
  void forward_user_to_frontend_homepage_on_authentication_success() throws Exception {
    // given
    httpServletRequest.setSession(new MockHttpSession());

    // when
    underTest.onAuthenticationSuccess(httpServletRequest, httpServletResponse, authentication);

    // then
    assertThat(httpServletResponse.getStatus()).isEqualTo(HttpStatus.FOUND.value());
    assertThat(httpServletResponse.getRedirectedUrl()).isEqualTo(Endpoints.Frontend.DASHBOARD);
  }

  @Test
  @DisplayName("Forward user to requested page on successful authentication")
  void forward_user_to_requested_page_on_authentication_success() throws Exception {
    // given
    MockHttpSession session = new MockHttpSession();
    session.setAttribute(SAVED_REQUEST_ATTRIBUTE, new DefaultSavedRequest.Builder().build());
    httpServletRequest.setSession(session);

    // when
    underTest.onAuthenticationSuccess(httpServletRequest, httpServletResponse, authentication);

    // then
    verify(savedRequestRedirectionHandler).onAuthenticationSuccess(httpServletRequest, httpServletResponse, authentication);
  }

}
