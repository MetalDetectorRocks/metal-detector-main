package rocks.metaldetector.web.controller.rest;

import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rocks.metaldetector.service.summary.SummaryService;
import rocks.metaldetector.support.Endpoints;
import rocks.metaldetector.web.api.response.SummaryResponse;

@RestController
@RequestMapping(Endpoints.Rest.HOME)
@AllArgsConstructor
public class SummaryRestController {

  private final SummaryService summaryService;

  @GetMapping(produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<SummaryResponse> handleSummaryRequest() {
    SummaryResponse summaryResponse = summaryService.createSummaryResponse();
    return ResponseEntity.ok(summaryResponse);
  }
}
