package rocks.metaldetector.service.dashboard;

import rocks.metaldetector.butler.facade.dto.ReleaseDto;
import rocks.metaldetector.support.TimeRange;
import rocks.metaldetector.web.api.response.DashboardResponse;

import java.util.List;

public interface DashboardService {

  DashboardResponse createDashboardResponse();

  List<ReleaseDto> findTopReleases(TimeRange timeRange, int minFollower, int maxReleases);
}
