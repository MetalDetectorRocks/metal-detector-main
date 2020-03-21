package rocks.metaldetector.butler.facade;

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
import rocks.metaldetector.butler.api.ButlerReleasesRequest;
import rocks.metaldetector.butler.api.ButlerReleasesResponse;

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
  public List<ReleaseDto> getReleases(ButlerReleasesRequest request) {
    HttpEntity<ButlerReleasesRequest> requestEntity = createHttpEntity(request);
    ResponseEntity<ButlerReleasesResponse> responseEntity = restTemplate.postForEntity(allReleasesUrl, requestEntity, ButlerReleasesResponse.class);

    ButlerReleasesResponse response = responseEntity.getBody();
    boolean shouldNotHappen = response == null || responseEntity.getStatusCode() != HttpStatus.OK;

    if (shouldNotHappen || response.getReleases().isEmpty()) {
      if (shouldNotHappen)
        log.warn("Could not get releases for request: " + request + ". Response: " + responseEntity.getStatusCode());
      return Collections.emptyList();
    }

    return response.getReleases();
  }

  private HttpEntity<ButlerReleasesRequest> createHttpEntity(ButlerReleasesRequest request) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.setAcceptCharset(Collections.singletonList(Charset.defaultCharset()));
    return new HttpEntity<>(request, headers);
  }
}
