package rocks.metaldetector.security.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;

import java.io.IOException;

import static rocks.metaldetector.support.Endpoints.Frontend.HOME;

public class CustomLogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler implements LogoutSuccessHandler {

  @Override
  public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
    /*
     * Maybe track the last page the user has visited and show this page again after next login
     * String refererUrl = request.getHeader("Referer");
     */
    response.sendRedirect(HOME);
  }
}
