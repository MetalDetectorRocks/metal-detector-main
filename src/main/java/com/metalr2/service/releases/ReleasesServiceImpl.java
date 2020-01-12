package com.metalr2.service.releases;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.metalr2.web.dto.releases.ReleasesRequest;
import com.metalr2.web.dto.releases.ReleasesResponse;
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
public class ReleasesServiceImpl implements ReleasesService {

  private static final String ALL_RELEASES_URL_FRAGMENT = "rest/v1/releases/all";
  private static final String BASE_URL_BUTLER = "http://localhost:8095/metal-release-butler/";

  private final RestTemplate restTemplate;
  private final ObjectMapper mapper;

  @Autowired
  public ReleasesServiceImpl(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
    this.mapper = new ObjectMapper();
  }

  @Override
  public Optional<ReleasesResponse> getReleases(ReleasesRequest request) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAcceptCharset(Collections.singletonList(Charset.defaultCharset()));
    HttpEntity<String> requestEntity;

    try {
      requestEntity = new HttpEntity<>(mapper.writeValueAsString(request), headers);
    } catch (JsonProcessingException e){
      return Optional.empty();
    }

    ResponseEntity<ReleasesResponse> responseEntity = restTemplate.postForEntity(BASE_URL_BUTLER + ALL_RELEASES_URL_FRAGMENT,
                                                                                 requestEntity, ReleasesResponse.class);

    ReleasesResponse response = responseEntity.getBody();
    if (response == null || responseEntity.getStatusCode() != HttpStatus.OK || !response.getReleases().iterator().hasNext()) {
      return Optional.empty();
    }

    return Optional.of(response);
  }
}
