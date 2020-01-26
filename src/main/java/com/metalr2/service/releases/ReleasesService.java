package com.metalr2.service.releases;

import com.metalr2.web.dto.releases.ReleaseDto;
import com.metalr2.web.dto.releases.ReleasesRequest;

import java.util.List;

public interface ReleasesService {

  List<ReleaseDto> getReleases(ReleasesRequest request);

}
