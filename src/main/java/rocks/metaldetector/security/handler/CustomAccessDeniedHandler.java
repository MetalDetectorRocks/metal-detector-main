package rocks.metaldetector.security.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.AccessDeniedHandler;
import rocks.metaldetector.config.constants.Endpoints;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.function.Supplier;

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
      // redirect to frontend home page if guest homepage is requested
      if (Endpoints.Guest.ALL_GUEST_INDEX_PAGES.contains(requestURI)) {
        httpServletResponse.setStatus(HttpStatus.TEMPORARY_REDIRECT.value());
        httpServletResponse.setHeader(HttpHeaders.LOCATION, httpServletRequest.getContextPath() + Endpoints.Frontend.HOME);
      }
      // redirect to status page if an authentication page is requested
      else if (Endpoints.Guest.ALL_AUTH_PAGES.contains(requestURI)) {
        httpServletResponse.setStatus(HttpStatus.TEMPORARY_REDIRECT.value());
        httpServletResponse.setHeader(HttpHeaders.LOCATION, httpServletRequest.getContextPath() + Endpoints.Frontend.STATUS);
      }
      // redirect to error page
      else {
        log.warn("Not authorized to access page {} for user with username {}", requestURI, authentication.getName());
        httpServletResponse.sendError(HttpStatus.FORBIDDEN.value(), "Not authorized");
      }
    }
  }
}
