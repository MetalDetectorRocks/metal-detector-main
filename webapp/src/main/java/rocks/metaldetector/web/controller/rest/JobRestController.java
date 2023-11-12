package rocks.metaldetector.web.controller.rest;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import rocks.metaldetector.butler.facade.dto.ImportJobResultDto;
import rocks.metaldetector.service.imports.JobService;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static rocks.metaldetector.support.Endpoints.Rest.COVER_JOB;
import static rocks.metaldetector.support.Endpoints.Rest.IMPORT_JOB;

@RestController
@AllArgsConstructor
public class JobRestController {

  private final JobService jobService;

  @PostMapping(path = IMPORT_JOB)
  public ResponseEntity<Void> createImportJob() {
    jobService.createImportJobs();
    return ResponseEntity.status(CREATED).build();
  }

  @GetMapping(path = IMPORT_JOB, produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<List<ImportJobResultDto>> getImportJobResults() {
    List<ImportJobResultDto> response = jobService.queryImportJobs();
    return ResponseEntity.ok(response);
  }

  @PostMapping(path = COVER_JOB)
  public ResponseEntity<Void> createRetryCoverDownloadJob() {
    jobService.createRetryCoverDownloadJob();
    return ResponseEntity.ok().build();
  }
}
