package com.metalr2.service.releases;

import com.metalr2.web.dto.releases.ReleaseDto;
import com.metalr2.web.dto.releases.ReleasesButlerRequest;

import java.util.List;

public interface ReleasesService {

  List<ReleaseDto> getReleases(ReleasesButlerRequest request);

}
