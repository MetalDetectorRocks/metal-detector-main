package rocks.metaldetector.service.releases;

import rocks.metaldetector.web.dto.request.ButlerReleasesRequest;
import rocks.metaldetector.web.dto.releases.ReleaseDto;

import java.util.List;

public interface ReleasesService {

  List<ReleaseDto> getReleases(ButlerReleasesRequest request);

}
