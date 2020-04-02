package rocks.metaldetector.support.infrastructure;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;
import java.net.URI;

@Slf4j
public class CustomClientErrorHandler implements ResponseErrorHandler {

  @Override
  public boolean hasError(ClientHttpResponse response) throws IOException {
    return response.getStatusCode().is4xxClientError() || response.getStatusCode().is5xxServerError();
  }

  @Override
  public void handleError(ClientHttpResponse clientHttpResponse) {
  }

  @Override
  public void handleError(URI url, HttpMethod method, ClientHttpResponse response) throws IOException {
    var logMessage = "URL: " + url.toString() + " | " +
            "Method: " + method.name() + " | " +
            "Status code: " + response.getStatusCode().value() + " | " +
            "Status text: " + response.getStatusText();

    if (response.getStatusCode().is5xxServerError()) {
      log.error(logMessage);
    }
    else {
      log.warn(logMessage);
    }
  }
}
