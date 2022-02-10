package rocks.metaldetector.security.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.function.Supplier;

import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.TEMPORARY_REDIRECT;
import static rocks.metaldetector.support.Endpoints.Authentication.ALL_AUTH_PAGES;
import static rocks.metaldetector.support.Endpoints.Frontend.STATUS;

@Slf4j
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

  private final Supplier<Authentication> authenticationSupplier;

  public CustomAccessDeniedHandler(Supplier<Authentication> authenticationSupplier) {
    this.authenticationSupplier = authenticationSupplier;
  }

  @Override
  public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AccessDeniedException e) throws IOException {
    Authentication authentication = authenticationSupplier.get();
    String requestURI = httpServletRequest.getRequestURI();

    // All anonymous requests are handled by spring security
    if (authentication instanceof UsernamePasswordAuthenticationToken) {
      // redirect to status page if an authentication page is requested
      if (ALL_AUTH_PAGES.contains(requestURI)) {
        httpServletResponse.setStatus(TEMPORARY_REDIRECT.value());
        httpServletResponse.setHeader(LOCATION, httpServletRequest.getContextPath() + STATUS);
      }
      // redirect to error page
      else {
        log.warn("Not authorized to access page {} for user with username {}", requestURI, authentication.getName());
        httpServletResponse.sendError(FORBIDDEN.value(), "Not authorized");
      }
    }
  }
}
