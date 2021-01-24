package rocks.metaldetector.butler.facade;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import rocks.metaldetector.butler.api.ButlerImportJob;
import rocks.metaldetector.butler.api.ButlerReleasesRequest;
import rocks.metaldetector.butler.api.ButlerReleasesResponse;
import rocks.metaldetector.butler.client.ReleaseButlerRestClient;
import rocks.metaldetector.butler.client.transformer.ButlerImportJobTransformer;
import rocks.metaldetector.butler.client.transformer.ButlerReleaseRequestTransformer;
import rocks.metaldetector.butler.client.transformer.ButlerReleaseResponseTransformer;
import rocks.metaldetector.butler.client.transformer.ButlerSortTransformer;
import rocks.metaldetector.butler.facade.dto.ImportJobResultDto;
import rocks.metaldetector.butler.facade.dto.ReleaseDto;
import rocks.metaldetector.support.Page;
import rocks.metaldetector.support.PageRequest;
import rocks.metaldetector.support.TimeRange;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ReleaseServiceImpl implements ReleaseService {

  private final ReleaseButlerRestClient butlerClient;
  private final ButlerReleaseRequestTransformer queryRequestTransformer;
  private final ButlerSortTransformer sortTransformer;
  private final ButlerReleaseResponseTransformer queryResponseTransformer;
  private final ButlerImportJobTransformer importJobResponseTransformer;

  @Override
  public List<ReleaseDto> findAllReleases(Iterable<String> artists, TimeRange timeRange) {
    ButlerReleasesRequest request = queryRequestTransformer.transform(artists, timeRange, null);
    ButlerReleasesResponse response = butlerClient.queryAllReleases(request);
    return queryResponseTransformer.transformToList(response);
  }

  @Override
  public Page<ReleaseDto> findReleases(Iterable<String> artists, TimeRange timeRange, PageRequest pageRequest) {
    ButlerReleasesRequest request = queryRequestTransformer.transform(artists, timeRange, pageRequest);
    String sortString = sortTransformer.transform(pageRequest.getSort());
    ButlerReleasesResponse response = butlerClient.queryReleases(request, sortString);
    return queryResponseTransformer.transformToPage(response);
  }

  @Override
  public void createImportJob() {
    butlerClient.createImportJob();
  }

  @Override
  public void createRetryCoverDownloadJob() {
    butlerClient.createRetryCoverDownloadJob();
  }

  @Override
  public List<ImportJobResultDto> queryImportJobResults() {
    List<ButlerImportJob> importJobResponses = butlerClient.queryImportJobResults();
    return importJobResponses.stream().map(importJobResponseTransformer::transform).collect(Collectors.toList());
  }

  @Override
  public void updateReleaseState(long releaseId, String state) {
    butlerClient.updateReleaseState(releaseId, state);
  }
}
