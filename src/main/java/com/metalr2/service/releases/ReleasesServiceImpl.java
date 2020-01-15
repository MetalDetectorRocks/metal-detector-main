package com.metalr2.service.releases;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.metalr2.config.misc.ReleaseButlerConfig;
import com.metalr2.web.dto.releases.ReleasesRequest;
import com.metalr2.web.dto.releases.ReleasesResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Optional;

@Service
@Slf4j
public class ReleasesServiceImpl implements ReleasesService {

  static final String ALL_RELEASES_URL_FRAGMENT = "/rest/v1/releases";

  private final RestTemplate restTemplate;
  private final ReleaseButlerConfig releaseButlerConfig;
  private final ObjectMapper mapper;

  @Autowired
  public ReleasesServiceImpl(RestTemplate restTemplate, ReleaseButlerConfig releaseButlerConfig, ObjectMapper mapper) {
    this.restTemplate = restTemplate;
    this.releaseButlerConfig = releaseButlerConfig;
    this.mapper = mapper;
  }

  @Override
  public Optional<ReleasesResponse> getReleases(ReleasesRequest request) {
    String requestBody = mapRequestBody(request);

    if (requestBody == null) {
      return Optional.empty();
    }

    HttpEntity<String> requestEntity = createHttpEntity(requestBody);
    ResponseEntity<ReleasesResponse> responseEntity = restTemplate.postForEntity(releaseButlerConfig.getRestBaseUrl() + ALL_RELEASES_URL_FRAGMENT,
                                                                                 requestEntity, ReleasesResponse.class);

    ReleasesResponse response = responseEntity.getBody();
    if (response == null || responseEntity.getStatusCode() != HttpStatus.OK || !response.getReleases().iterator().hasNext()) {
      return Optional.empty();
    }

    return Optional.of(response);
  }

  private String mapRequestBody(ReleasesRequest request) {
    String requestBody;

    try {
      requestBody = mapper.writeValueAsString(request);
    }
    catch (JsonProcessingException e) {
      log.warn("Exception parsing release request: " + request.toString());
      return null;
    }

    return requestBody;
  }

  private HttpEntity<String> createHttpEntity(String requestBody) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.setAcceptCharset(Collections.singletonList(Charset.defaultCharset()));
    return new HttpEntity<>(requestBody, headers);
  }
}
