package rocks.metaldetector.butler.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import rocks.metaldetector.butler.api.ButlerReleasesRequest;
import rocks.metaldetector.butler.api.ButlerReleasesResponse;
import rocks.metaldetector.support.ExternalServiceException;

import java.nio.charset.Charset;
import java.util.Collections;

@Service
@Slf4j
@Profile({"default", "preview", "prod"})
public class ReleaseButlerRestClientImpl implements ReleaseButlerRestClient {

  private final RestTemplate restTemplate;
  private final String releasesEndpoint;

  @Autowired
  public ReleaseButlerRestClientImpl(RestTemplate restTemplate, String releasesEndpoint) {
    this.restTemplate = restTemplate;
    this.releasesEndpoint = releasesEndpoint;
  }

  @Override
  public ButlerReleasesResponse queryReleases(ButlerReleasesRequest request) {
    HttpEntity<ButlerReleasesRequest> requestEntity = createHttpEntity(request);

    ResponseEntity<ButlerReleasesResponse> responseEntity = restTemplate.postForEntity(releasesEndpoint, requestEntity, ButlerReleasesResponse.class);
    ButlerReleasesResponse response = responseEntity.getBody();

    var shouldNotHappen = response == null || !responseEntity.getStatusCode().is2xxSuccessful();
    if (shouldNotHappen) {
      throw new ExternalServiceException("Could not get releases for request: " + request + "' (Response code: " + responseEntity.getStatusCode() + ")");
    }

    return response;
  }

  private HttpEntity<ButlerReleasesRequest> createHttpEntity(ButlerReleasesRequest request) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.setAcceptCharset(Collections.singletonList(Charset.defaultCharset()));
    return new HttpEntity<>(request, headers);
  }
}
