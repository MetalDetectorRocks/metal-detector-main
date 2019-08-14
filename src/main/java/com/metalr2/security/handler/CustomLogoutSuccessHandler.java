package com.metalr2.security.handler;

import com.metalr2.config.constants.Endpoints;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CustomLogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler implements LogoutSuccessHandler {

  @Override
  public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
    /*
     * Maybe track the last page the user has visited and show this page again after next login
     * String refererUrl = request.getHeader("Referer");
     */
    response.setStatus(HttpStatus.OK.value());
    response.sendRedirect(Endpoints.Guest.LOGIN + "?logout");
  }
}
