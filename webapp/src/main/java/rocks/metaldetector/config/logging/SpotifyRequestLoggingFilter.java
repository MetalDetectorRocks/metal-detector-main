package rocks.metaldetector.config.logging;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.web.filter.CommonsRequestLoggingFilter;
import rocks.metaldetector.support.Endpoints;
import rocks.metaldetector.support.infrastructure.WithSensitiveDataRemover;

import javax.servlet.http.HttpServletRequest;

public class SpotifyRequestLoggingFilter extends CommonsRequestLoggingFilter implements WithSensitiveDataRemover {

  SpotifyRequestLoggingFilter() {
    super.setIncludeQueryString(true);
    super.setIncludePayload(true);
    super.setIncludeClientInfo(true);
    super.setMaxPayloadLength(10000);
    super.setIncludeHeaders(false);
    super.setAfterMessagePrefix("");
  }

  @Override
  protected boolean shouldLog(HttpServletRequest request) {
    var requestUri = request.getRequestURI().toLowerCase();
    return requestUri.startsWith(Endpoints.Frontend.SPOTIFY_SYNCHRONIZATION);
  }

  @Override
  protected void afterRequest(HttpServletRequest request, String message) {
    try {
      message = removeSensitiveDataFromPayload(message);
      logger.info(message);
    }
    catch (JsonProcessingException e) {
      logger.error(e);
    }
  }
}
