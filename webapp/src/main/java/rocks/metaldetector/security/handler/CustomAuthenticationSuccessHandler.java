package rocks.metaldetector.security.handler;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.DefaultSavedRequest;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import static rocks.metaldetector.support.Endpoints.Frontend.HOME;

public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

  static final String SAVED_REQUEST_ATTRIBUTE = "SPRING_SECURITY_SAVED_REQUEST";

  private final SavedRequestAwareAuthenticationSuccessHandler savedRequestRedirectionHandler;

  public CustomAuthenticationSuccessHandler(SavedRequestAwareAuthenticationSuccessHandler savedRequestRedirectionHandler) {
    this.savedRequestRedirectionHandler = savedRequestRedirectionHandler;
  }

  @Override
  public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws ServletException, IOException {
    handleRedirect(httpServletRequest, httpServletResponse, authentication);
  }

  /*
   * We distinguish between two cases:
   * (1) The user makes a normal login and is then forwarded to the homepage.
   * (2) The user requested a specific page, but must first log in. He or she is then redirected to the initially requested page.
   */
  private void handleRedirect(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws ServletException, IOException {
    HttpSession session = httpServletRequest.getSession(false);
    DefaultSavedRequest savedRequest = (DefaultSavedRequest) session.getAttribute(SAVED_REQUEST_ATTRIBUTE);

    if (savedRequest == null) {
      // redirect to home page for authenticated users
      httpServletResponse.sendRedirect(HOME);
    }
    else {
      // redirect to requested page
      savedRequestRedirectionHandler.onAuthenticationSuccess(httpServletRequest, httpServletResponse, authentication);
    }
  }
}
