package rocks.metaldetector.butler.facade;

import rocks.metaldetector.butler.api.ButlerReleasesRequest;

import java.util.List;

public interface ReleasesService {

  List<ReleaseDto> getReleases(ButlerReleasesRequest request);

}
