package com.metalr2.config.logging;

import org.springframework.web.filter.CommonsRequestLoggingFilter;

import javax.servlet.http.HttpServletRequest;

public class RestRequestLoggingFilter extends CommonsRequestLoggingFilter {

  public RestRequestLoggingFilter() {
    super.setIncludeQueryString(true);
    super.setIncludeClientInfo(true);
    super.setIncludeHeaders(false);
    super.setIncludePayload(true);
    super.setMaxPayloadLength(10000);
    super.setAfterMessagePrefix("Request data: ");
  }

  @Override
  protected boolean shouldLog(HttpServletRequest request) {
    return request.getRequestURI().toLowerCase().startsWith("/rest");
  }

  @Override
  protected void beforeRequest(HttpServletRequest request, String message) {
    // do nothing
  }

  @Override
  protected void afterRequest(HttpServletRequest request, String message) {
    logger.info(message);
  }

}
