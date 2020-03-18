package rocks.metaldetector.service.releases;

import rocks.metaldetector.web.dto.releases.ButlerReleasesRequest;
import rocks.metaldetector.web.dto.releases.ReleaseDto;

import java.util.List;

public interface ReleasesService {

  List<ReleaseDto> getReleases(ButlerReleasesRequest request);

}
