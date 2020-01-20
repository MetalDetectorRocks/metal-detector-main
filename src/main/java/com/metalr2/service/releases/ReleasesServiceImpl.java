package com.metalr2.service.releases;

import com.metalr2.web.dto.releases.ReleaseDto;
import com.metalr2.web.dto.releases.ReleasesRequest;
import com.metalr2.web.dto.releases.ReleasesResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class ReleasesServiceImpl implements ReleasesService {

  private final RestTemplate restTemplate;
  private final String allReleasesUrl;

  @Autowired
  public ReleasesServiceImpl(RestTemplate restTemplate, @Value("${metal.release.buter.unpaginated.releases.endpoint}") String allReleasesUrl) {
    this.restTemplate = restTemplate;
    this.allReleasesUrl = allReleasesUrl;
  }

  @Override
  public List<ReleaseDto> getReleases(ReleasesRequest request) {
    HttpEntity<ReleasesRequest> requestEntity = createHttpEntity(request);
    ResponseEntity<ReleasesResponse> responseEntity = restTemplate.postForEntity(allReleasesUrl, requestEntity, ReleasesResponse.class);

    ReleasesResponse response = responseEntity.getBody();
    if (response == null || responseEntity.getStatusCode() != HttpStatus.OK || response.getReleases().isEmpty()) {
      return Collections.emptyList();
    }

    return response.getReleases();
  }

  private HttpEntity<ReleasesRequest> createHttpEntity(ReleasesRequest request) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.setAcceptCharset(Collections.singletonList(Charset.defaultCharset()));
    return new HttpEntity<>(request, headers);
  }
}
