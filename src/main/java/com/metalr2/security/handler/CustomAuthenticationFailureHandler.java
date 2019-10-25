package com.metalr2.security.handler;

import com.metalr2.config.constants.Endpoints;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

  @Override
  public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
    String redirectURL = exception instanceof DisabledException ? Endpoints.Guest.LOGIN + "?disabled" : Endpoints.Guest.LOGIN + "?badCredentials";
    response.sendRedirect(redirectURL);
  }
}
