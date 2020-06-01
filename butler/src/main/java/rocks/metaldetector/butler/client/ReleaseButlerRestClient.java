package rocks.metaldetector.butler.client;

import rocks.metaldetector.butler.api.ButlerImportJobResponse;
import rocks.metaldetector.butler.api.ButlerReleasesRequest;
import rocks.metaldetector.butler.api.ButlerReleasesResponse;

import java.util.List;

public interface ReleaseButlerRestClient {

  ButlerReleasesResponse queryReleases(ButlerReleasesRequest request);

  void createImportJob();

  List<ButlerImportJobResponse> queryImportJobResults();

}
