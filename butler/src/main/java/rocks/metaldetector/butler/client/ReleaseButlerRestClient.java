package rocks.metaldetector.butler.client;

import rocks.metaldetector.butler.api.ButlerImportResponse;
import rocks.metaldetector.butler.api.ButlerReleasesRequest;
import rocks.metaldetector.butler.api.ButlerReleasesResponse;

public interface ReleaseButlerRestClient {

  ButlerReleasesResponse queryReleases(ButlerReleasesRequest request);

  ButlerImportResponse importReleases();

}
