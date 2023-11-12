package rocks.metaldetector.butler.client;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestOperations;
import rocks.metaldetector.butler.api.ButlerImportCreatedResponse;
import rocks.metaldetector.butler.api.ButlerImportJob;
import rocks.metaldetector.butler.api.ButlerImportResponse;
import rocks.metaldetector.butler.api.ButlerReleasesRequest;
import rocks.metaldetector.butler.api.ButlerReleasesResponse;
import rocks.metaldetector.butler.api.ButlerStatisticsResponse;
import rocks.metaldetector.butler.api.ButlerUpdateReleaseStateRequest;
import rocks.metaldetector.butler.config.ButlerConfig;
import rocks.metaldetector.support.exceptions.ExternalServiceException;

import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpMethod.PUT;

@Service
@Slf4j
@Profile({"default", "preview", "prod"})
@AllArgsConstructor
public class ReleaseButlerRestClientImpl implements ReleaseButlerRestClient {

  static final String UPDATE_ENDPOINT_PATH_PARAM = "/{releaseId}";

  private final RestOperations releaseButlerRestOperations;
  private final ButlerConfig butlerConfig;

  @Override
  public ButlerReleasesResponse queryAllReleases(ButlerReleasesRequest request) {
    HttpEntity<ButlerReleasesRequest> requestEntity = new HttpEntity<>(request);

    ResponseEntity<ButlerReleasesResponse> responseEntity = releaseButlerRestOperations.postForEntity(
        butlerConfig.getUnpaginatedReleasesUrl(),
        requestEntity,
        ButlerReleasesResponse.class
    );

    return handleReleaseResponseEntity(request, responseEntity);
  }

  @Override
  public ButlerReleasesResponse queryReleases(ButlerReleasesRequest request, String sort) {
    HttpEntity<ButlerReleasesRequest> requestEntity = new HttpEntity<>(request);
    ResponseEntity<ButlerReleasesResponse> responseEntity = releaseButlerRestOperations.postForEntity(
        createReleaseUrlWithParameter(sort),
        requestEntity,
        ButlerReleasesResponse.class
    );

    return handleReleaseResponseEntity(request, responseEntity);
  }

  private String createReleaseUrlWithParameter(String sortParam) {
    return sortParam != null && !sortParam.isBlank() ? butlerConfig.getReleasesUrl() + "?" + sortParam : butlerConfig.getReleasesUrl();
  }

  private ButlerReleasesResponse handleReleaseResponseEntity(ButlerReleasesRequest request, ResponseEntity<ButlerReleasesResponse> responseEntity) {
    ButlerReleasesResponse response = responseEntity.getBody();

    var shouldNotHappen = response == null || !responseEntity.getStatusCode().is2xxSuccessful();
    if (shouldNotHappen) {
      throw new ExternalServiceException("Could not get releases for request: '" + request + "' (Response code: " + responseEntity.getStatusCode() + ")");
    }

    return response;
  }

  @Override
  public ButlerImportCreatedResponse createImportJobs() {
    ResponseEntity<ButlerImportCreatedResponse> responseEntity = releaseButlerRestOperations.postForEntity(butlerConfig.getImportUrl(), null, ButlerImportCreatedResponse.class);
    ButlerImportCreatedResponse response = responseEntity.getBody();

    var shouldNotHappen = response == null || !responseEntity.getStatusCode().is2xxSuccessful();
    if (shouldNotHappen) {
      throw new ExternalServiceException("Could not create import jobs' (Response code: " + responseEntity.getStatusCode() + ")");
    }

    return response;
  }

  @Override
  public void createRetryCoverDownloadJob() {
    ResponseEntity<Void> responseEntity = releaseButlerRestOperations.postForEntity(butlerConfig.getRetryCoverDownloadUrl(), null, Void.class);
    if (!responseEntity.getStatusCode().is2xxSuccessful()) {
      throw new ExternalServiceException("Could not retry cover download (Response code: " + responseEntity.getStatusCode() + ")");
    }
  }

  @Override
  public List<ButlerImportJob> queryImportJobs() {
    ResponseEntity<ButlerImportResponse> responseEntity = releaseButlerRestOperations.getForEntity(
        butlerConfig.getImportUrl(),
        ButlerImportResponse.class
    );

    ButlerImportResponse response = responseEntity.getBody();
    var shouldNotHappen = response == null || !responseEntity.getStatusCode().is2xxSuccessful();
    if (shouldNotHappen) {
      throw new ExternalServiceException("Could not fetch import jobs (Response code: " + responseEntity.getStatusCode() + ")");
    }

    return response.getImportJobs();
  }

  @Override
  public ButlerImportJob queryImportJob(String jobId) {
    ResponseEntity<ButlerImportJob> responseEntity = releaseButlerRestOperations.getForEntity(
        butlerConfig.getImportUrl() + "/" + jobId,
        ButlerImportJob.class
    );

    ButlerImportJob response = responseEntity.getBody();
    var shouldNotHappen = response == null || !responseEntity.getStatusCode().is2xxSuccessful();
    if (shouldNotHappen) {
      throw new ExternalServiceException("Could not fetch import job (Response code: " + responseEntity.getStatusCode() + ")");
    }

    return response;
  }

  @Override
  public void updateReleaseState(long releaseId, String state) {
    ButlerUpdateReleaseStateRequest request = ButlerUpdateReleaseStateRequest.builder().state(state.toUpperCase()).build();
    HttpEntity<ButlerUpdateReleaseStateRequest> httpEntity = new HttpEntity<>(request);
    ResponseEntity<Void> responseEntity = releaseButlerRestOperations.exchange(butlerConfig.getReleasesUrl() + UPDATE_ENDPOINT_PATH_PARAM,
                                                                               PUT,
                                                                               httpEntity,
                                                                               Void.class,
                                                                               Map.of("releaseId", releaseId));
    var shouldNotHappen = !responseEntity.getStatusCode().is2xxSuccessful();
    if (shouldNotHappen) {
      throw new ExternalServiceException("Could not update release state (Response code: " + responseEntity.getStatusCode() + ")");
    }
  }

  @Override
  public ButlerStatisticsResponse getStatistics() {
    ResponseEntity<ButlerStatisticsResponse> responseEntity = releaseButlerRestOperations.getForEntity(butlerConfig.getStatisticsUrl(), ButlerStatisticsResponse.class);
    ButlerStatisticsResponse response = responseEntity.getBody();

    var shouldNotHappen = response == null || !responseEntity.getStatusCode().is2xxSuccessful();
    if (shouldNotHappen) {
      throw new ExternalServiceException("Could not get statistics (Response code: " + responseEntity.getStatusCode() + ")");
    }
    return response;
  }
}
