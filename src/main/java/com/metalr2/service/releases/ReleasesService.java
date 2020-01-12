package com.metalr2.service.releases;

import com.metalr2.web.dto.releases.ReleasesRequest;
import com.metalr2.web.dto.releases.ReleasesResponse;

import java.util.Optional;

public interface ReleasesService {

  Optional<ReleasesResponse> getReleases(ReleasesRequest request);

}
