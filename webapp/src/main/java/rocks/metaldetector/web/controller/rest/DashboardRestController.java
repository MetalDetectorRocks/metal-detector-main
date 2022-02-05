package rocks.metaldetector.web.controller.rest;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import rocks.metaldetector.service.dashboard.DashboardService;
import rocks.metaldetector.web.api.response.DashboardResponse;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static rocks.metaldetector.support.Endpoints.Rest.DASHBOARD;

@RestController
@AllArgsConstructor
public class DashboardRestController {

  private final DashboardService dashboardService;

  @GetMapping(path = DASHBOARD, produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<DashboardResponse> handleSummaryRequest() {
    DashboardResponse dashboardResponse = dashboardService.createDashboardResponse();
    return ResponseEntity.ok(dashboardResponse);
  }
}
