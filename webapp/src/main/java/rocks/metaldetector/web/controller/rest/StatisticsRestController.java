package rocks.metaldetector.web.controller.rest;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import rocks.metaldetector.service.statistics.StatisticsService;
import rocks.metaldetector.web.api.response.StatisticsResponse;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static rocks.metaldetector.support.Endpoints.Rest.STATISTICS;

@RestController
public class StatisticsRestController {

  private final StatisticsService statisticsService;

  public StatisticsRestController(@Qualifier("statisticsServiceImpl") StatisticsService statisticsService) {
    this.statisticsService = statisticsService;
  }

  @GetMapping(path = STATISTICS, produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<StatisticsResponse> handleStatisticsRequest() {
    StatisticsResponse statisticsResponse = statisticsService.createStatisticsResponse();
    return ResponseEntity.ok(statisticsResponse);
  }
}
