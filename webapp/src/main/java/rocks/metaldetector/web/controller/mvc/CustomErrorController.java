package rocks.metaldetector.web.controller.mvc;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import rocks.metaldetector.config.constants.ViewNames;
import rocks.metaldetector.support.Endpoints;

import java.util.Map;

@Controller
@Slf4j
public class CustomErrorController implements ErrorController {

  @RequestMapping(Endpoints.ERROR)
  public ModelAndView handleError(HttpServletRequest request) {
    Object statusCodeObj = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
    Object requestURIObj = request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);

    int statusCode = statusCodeObj != null ? Integer.parseInt(statusCodeObj.toString()) : -1;
    String requestedURI = requestURIObj != null ? (String) requestURIObj : "";

    if (statusCode == HttpStatus.NOT_FOUND.value()) {
      log.warn("Could not find any content for '{}'", requestedURI);
      return new ModelAndView(ViewNames.Error.ERROR_404, Map.of("requestedURI", requestedURI), HttpStatus.NOT_FOUND);
    }
    else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
      log.error("Internal server error while requesting '{}''", requestedURI);
      return new ModelAndView(ViewNames.Error.ERROR_500, Map.of("requestedURI", requestedURI), HttpStatus.INTERNAL_SERVER_ERROR);
    }
    else if (statusCode == HttpStatus.FORBIDDEN.value()) {
      log.warn("Access denied while requesting '{}' for user {}'",
               requestedURI, request.getUserPrincipal() != null ? request.getUserPrincipal().getName() : "[Unknown]");
      return new ModelAndView(ViewNames.Error.ERROR_403, Map.of("requestedURI", requestedURI), HttpStatus.FORBIDDEN);
    }

    log.error("Unhandled exception occurred. Status code is {}. Requested URI was {}", statusCode, request);

    HttpStatus responseStatus = statusCode != -1 ? HttpStatus.valueOf(statusCode) : HttpStatus.I_AM_A_TEAPOT;
    return new ModelAndView(ViewNames.Error.ERROR, Map.of("requestedURI", requestedURI), responseStatus);
  }
}
