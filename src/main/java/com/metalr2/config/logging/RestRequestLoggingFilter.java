package com.metalr2.config.logging;

import org.springframework.web.filter.CommonsRequestLoggingFilter;

import javax.servlet.http.HttpServletRequest;

public class RestRequestLoggingFilter extends CommonsRequestLoggingFilter {

  public RestRequestLoggingFilter() {
    super.setIncludeQueryString(true);
    super.setIncludePayload(true);
    super.setIncludeClientInfo(true);
    super.setMaxPayloadLength(10000);
    super.setIncludeHeaders(false);
    super.setAfterMessagePrefix("");
  }

  @Override
  protected boolean shouldLog(HttpServletRequest request) {
    return request.getRequestURI().toLowerCase().startsWith("/rest");
  }

  @Override
  protected void beforeRequest(HttpServletRequest request, String message) {
  }

  @Override
  protected void afterRequest(HttpServletRequest request, String message) {
    logger.info(request.getMethod() + ": " + message);
  }

}
