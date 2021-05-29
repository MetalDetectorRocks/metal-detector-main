package rocks.metaldetector.web.controller.rest;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import rocks.metaldetector.butler.facade.JobService;
import rocks.metaldetector.butler.facade.dto.ImportJobResultDto;
import rocks.metaldetector.support.Endpoints;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@AllArgsConstructor
public class JobRestController {

  private final JobService jobService;

  @PreAuthorize("hasRole('ROLE_ADMINISTRATOR')")
  @PostMapping(path = Endpoints.Rest.IMPORT_JOB)
  public ResponseEntity<Void> createImportJob() {
    jobService.createImportJob();
    return ResponseEntity.status(CREATED).build();
  }

  @PreAuthorize("hasRole('ROLE_ADMINISTRATOR')")
  @GetMapping(path = Endpoints.Rest.IMPORT_JOB,
      produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<List<ImportJobResultDto>> getImportJobResults() {
    List<ImportJobResultDto> response = jobService.queryImportJobResults();
    return ResponseEntity.ok(response);
  }

  @PreAuthorize("hasRole('ROLE_ADMINISTRATOR')")
  @PostMapping(path = Endpoints.Rest.COVER_JOB)
  public ResponseEntity<Void> createRetryCoverDownloadJob() {
    jobService.createRetryCoverDownloadJob();
    return ResponseEntity.ok().build();
  }
}
