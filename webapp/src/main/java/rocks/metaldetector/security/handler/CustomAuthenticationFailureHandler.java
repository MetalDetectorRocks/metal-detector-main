package rocks.metaldetector.security.handler;

import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import rocks.metaldetector.config.constants.Endpoints;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

  @Override
  public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
    String redirectURL;

    if (exception instanceof DisabledException) {
      redirectURL = Endpoints.Guest.LOGIN + "?disabled";
    }
    else if (exception instanceof InternalAuthenticationServiceException) {
      redirectURL = Endpoints.Guest.LOGIN + "?blocked";
    }
    else {
      redirectURL = Endpoints.Guest.LOGIN + "?badCredentials";
    }

    response.sendRedirect(redirectURL);
  }
}
