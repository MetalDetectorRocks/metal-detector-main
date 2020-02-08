package com.metalr2.config.resttemplate;

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
    log.warn("CustomClientErrorHandler" + " | URL: " + url.toString() + " | Method: " + method.name()
             + " | Status Code: " + response.getStatusCode().value() + " | Status Text: " + response.getStatusText());
  }
}