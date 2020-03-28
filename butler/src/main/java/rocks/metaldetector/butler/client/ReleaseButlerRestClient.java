package rocks.metaldetector.butler.client;

import rocks.metaldetector.butler.api.ButlerReleasesRequest;
import rocks.metaldetector.butler.api.ButlerReleasesResponse;
import rocks.metaldetector.butler.facade.dto.ReleaseDto;

import java.util.List;

public interface ReleaseButlerRestClient {

  ButlerReleasesResponse queryReleases(ButlerReleasesRequest request);

}
