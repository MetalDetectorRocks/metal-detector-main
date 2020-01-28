package com.metalr2.security;

import com.metalr2.config.constants.Endpoints;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

@Component
public class RedirectionHandlerInterceptor implements HandlerInterceptor {

  private final CurrentUserSupplier currentUserSupplier;

  @Autowired
  public RedirectionHandlerInterceptor(CurrentUserSupplier currentUserSupplier) {
    this.currentUserSupplier = currentUserSupplier;
  }

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
    if (Arrays.asList(Endpoints.AntPattern.AUTH_PAGES).contains(request.getRequestURI())
        && currentUserSupplier.get() != null) {
      response.setContentType("text/plain");
      sendRedirect(request, response);
      return false;
    }
    else {
      return true;
    }
  }

  @Override
  public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                         ModelAndView modelAndView) {
  }

  @Override
  public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                              Object handler, Exception exception) {
  }

  private void sendRedirect(HttpServletRequest request, HttpServletResponse response) {
    String encodedRedirectURL = response.encodeRedirectURL(request.getContextPath() + Endpoints.Frontend.HOME);
    response.setStatus(HttpStatus.TEMPORARY_REDIRECT.value());
    response.setHeader("Location", encodedRedirectURL);
  }
}