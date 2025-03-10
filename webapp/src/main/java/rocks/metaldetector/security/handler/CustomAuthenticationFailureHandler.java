package rocks.metaldetector.security.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.io.IOException;

import static rocks.metaldetector.support.Endpoints.Authentication.LOGIN;

public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

  @Override
  public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
    String redirectURL;

    if (exception instanceof DisabledException) {
      redirectURL = LOGIN + "?disabled";
    }
    else if (exception instanceof InternalAuthenticationServiceException) {
      redirectURL = LOGIN + "?blocked";
    }
    else {
      redirectURL = LOGIN + "?badCredentials";
    }

    response.sendRedirect(redirectURL);
  }
}
