package rocks.metaldetector.web.controller.rest;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rocks.metaldetector.support.Endpoints;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping(Endpoints.Rest.CSP_VIOLATION_REPORT)
@AllArgsConstructor
public class CspViolationReportRestController {

  static final String LOG_CSP_REPORT_PROPERTY = "log-csp-report";

  private final Environment environment;

  @PostMapping(consumes = "application/csp-report")
  public void handleCspViolationReport(@RequestBody Map<String, Object> report) {
    var logCspReport = Boolean.parseBoolean(environment.getProperty(LOG_CSP_REPORT_PROPERTY));
    if (logCspReport) {
      log.info(report.toString());
    }
  }
}
