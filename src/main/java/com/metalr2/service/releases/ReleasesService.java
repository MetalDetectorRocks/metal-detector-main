package com.metalr2.service.releases;

import com.metalr2.web.dto.releases.ButlerReleasesRequest;
import com.metalr2.web.dto.releases.ReleaseDto;

import java.util.List;

public interface ReleasesService {

  List<ReleaseDto> getReleases(ButlerReleasesRequest request);

}
