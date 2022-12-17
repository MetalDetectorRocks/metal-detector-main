package rocks.metaldetector.security.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;

import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.TEMPORARY_REDIRECT;
import static rocks.metaldetector.support.Endpoints.Authentication.ALL_AUTH_PAGES;
import static rocks.metaldetector.support.Endpoints.Frontend.STATUS;

@Slf4j
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

  @Override
  public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AccessDeniedException e) throws IOException {
    String requestURI = httpServletRequest.getRequestURI();

    if (ALL_AUTH_PAGES.contains(requestURI)) {
      httpServletResponse.setStatus(TEMPORARY_REDIRECT.value());
      httpServletResponse.setHeader(LOCATION, httpServletRequest.getContextPath() + STATUS);
    }
    else {
      log.warn("Not authorized to access page {}", requestURI);
      httpServletResponse.sendError(FORBIDDEN.value(), "Not authorized");
    }
  }
}
