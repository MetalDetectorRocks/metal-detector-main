package com.metalr2.web.controller;

import com.metalr2.config.constants.Endpoints;
import com.metalr2.config.constants.ViewNames;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

@Controller
@Slf4j
public class CustomErrorController implements org.springframework.boot.web.servlet.error.ErrorController {

  @Override
  public String getErrorPath() {
    return Endpoints.Guest.ERROR;
  }

  @RequestMapping(Endpoints.Guest.ERROR)
  public ModelAndView handleError(HttpServletRequest request) {
    Object statusCodeObj = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
    Object requestURIObj = request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);

    int statusCode    = (statusCodeObj != null)? Integer.parseInt(statusCodeObj.toString()) : -1;
    String requestedURI = (requestURIObj != null)? (String) requestURIObj : "";

    if(statusCode == HttpStatus.NOT_FOUND.value()) {
      log.warn("Could not find any content for '{}'", requestedURI);
      return new ModelAndView(ViewNames.Guest.ERROR_404, "requestedURI", requestedURI);
    }
    else if(statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
      log.error("Internal server error while requesting '{}''", requestedURI);
      return new ModelAndView(ViewNames.Guest.ERROR_500, "requestedURI", requestedURI);
    }

    log.error("Unhandled exception occurred. Status code is {}. Requested URI was {}", statusCode, request);
    return new ModelAndView(ViewNames.Guest.ERROR, "requestedURI", requestedURI);
  }
}
