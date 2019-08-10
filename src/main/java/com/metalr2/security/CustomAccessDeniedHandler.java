package com.metalr2.security;

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

@Slf4j
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

  @Override
  public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AccessDeniedException e) throws IOException {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String requestURI   = httpServletRequest.getRequestURI();

    if (auth instanceof UsernamePasswordAuthenticationToken) {
      httpServletResponse.setStatus(HttpStatus.TEMPORARY_REDIRECT.value());

      // redirect to home page if '/' is requested
      if (requestURI.equals(Endpoints.Guest.SLASH_INDEX)) {
        httpServletResponse.sendRedirect(httpServletRequest.getContextPath() + Endpoints.Frontend.HOME);
      }
      // redirect to status page if an auth page is requested
      else if (Endpoints.Guest.ALL_AUTH_PAGES.contains(requestURI)) {
        httpServletResponse.sendRedirect(httpServletRequest.getContextPath() + Endpoints.Frontend.STATUS);
      }
    }
    else {
      httpServletResponse.sendRedirect(httpServletRequest.getContextPath() + Endpoints.Guest.LOGIN);
    }
  }
}
