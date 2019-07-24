package com.metalr2.web.controller;

import com.metalr2.config.constants.Endpoints;
import com.metalr2.config.constants.ViewNames;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

@Controller
public class CustomErrorController implements org.springframework.boot.web.servlet.error.ErrorController {

  private static final Logger LOG = LoggerFactory.getLogger(CustomErrorController.class);

  @Override
  public String getErrorPath() {
    return Endpoints.Guest.ERROR;
  }

  @RequestMapping(Endpoints.Guest.ERROR)
  public ModelAndView handleError(HttpServletRequest request) {
    Object statusCodeObj = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
    Object requestURIObj = request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);

    int statusCode    = (statusCodeObj != null)? Integer.parseInt(statusCodeObj.toString()) : -1;
    String requestURI = (requestURIObj != null)? (String) requestURIObj : "";

    if(statusCode == HttpStatus.NOT_FOUND.value()) {
      LOG.warn("Could not find any content for '" + requestURI + "'.");
      return new ModelAndView(ViewNames.ERROR_404, "requestedURI", requestURI);
    }
    else if(statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
      LOG.error("Internal server error while requesting '" + requestURI + "'.");
      return new ModelAndView(ViewNames.Guest.ERROR_500, "requestedURI", requestURI);
    }

    return new ModelAndView(ViewNames.Guest.ERROR, "requestedURI", requestURI);
  }
}
