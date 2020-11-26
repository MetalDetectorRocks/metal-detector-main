package rocks.metaldetector.butler.client;

import rocks.metaldetector.butler.api.ButlerImportJob;
import rocks.metaldetector.butler.api.ButlerReleasesRequest;
import rocks.metaldetector.butler.api.ButlerReleasesResponse;

import java.util.List;

public interface ReleaseButlerRestClient {

  ButlerReleasesResponse queryAllReleases(ButlerReleasesRequest request);

  ButlerReleasesResponse queryReleases(ButlerReleasesRequest request);

  void createImportJob();

  void createRetryCoverDownloadJob();

  List<ButlerImportJob> queryImportJobResults();

  void updateReleaseState(long releaseId, String state);

}
