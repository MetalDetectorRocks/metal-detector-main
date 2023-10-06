package rocks.metaldetector.butler.client;

import rocks.metaldetector.butler.api.ButlerImportCreatedResponse;
import rocks.metaldetector.butler.api.ButlerImportJob;
import rocks.metaldetector.butler.api.ButlerReleasesRequest;
import rocks.metaldetector.butler.api.ButlerReleasesResponse;
import rocks.metaldetector.butler.api.ButlerStatisticsResponse;

import java.util.List;

public interface ReleaseButlerRestClient {

  ButlerReleasesResponse queryAllReleases(ButlerReleasesRequest request);

  ButlerReleasesResponse queryReleases(ButlerReleasesRequest request, String sort);

  ButlerImportCreatedResponse createImportJobs();

  void createRetryCoverDownloadJob();

  List<ButlerImportJob> queryImportJobs();

  ButlerImportJob queryImportJob(String jobId);

  void updateReleaseState(long releaseId, String state);

  ButlerStatisticsResponse getStatistics();
}
