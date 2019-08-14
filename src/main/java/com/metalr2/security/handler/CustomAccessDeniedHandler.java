package com.metalr2.security.handler;

import com.metalr2.config.constants.Endpoints;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Slf4j
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

  @Override
  public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AccessDeniedException e) throws IOException {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String requestURI = httpServletRequest.getRequestURI();

    if (authentication instanceof UsernamePasswordAuthenticationToken) {
      List<String> guestHomepages = List.of(Endpoints.Guest.INDEX, Endpoints.Guest.SLASH_INDEX, Endpoints.Guest.EMPTY_INDEX);

      // redirect to frontend home page if guest homepage is requested
      if (guestHomepages.contains(requestURI)) {
        httpServletResponse.setStatus(HttpStatus.TEMPORARY_REDIRECT.value());
        httpServletResponse.sendRedirect(httpServletRequest.getContextPath() + Endpoints.Frontend.HOME);
      }
      // redirect to status page if an authentication page is requested
      else if (Endpoints.Guest.ALL_AUTH_PAGES.contains(requestURI)) {
        httpServletResponse.setStatus(HttpStatus.TEMPORARY_REDIRECT.value());
        httpServletResponse.sendRedirect(httpServletRequest.getContextPath() + Endpoints.Frontend.STATUS);
      }
      // redirect to error page
      else {
        httpServletResponse.sendError(HttpStatus.FORBIDDEN.value(), "Not authorized");
      }
    }
    else {
      httpServletResponse.sendRedirect(httpServletRequest.getContextPath() + Endpoints.Guest.LOGIN);
    }
  }
}
