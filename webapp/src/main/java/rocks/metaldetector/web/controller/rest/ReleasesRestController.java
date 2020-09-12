package rocks.metaldetector.web.controller.rest;

import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import rocks.metaldetector.butler.facade.ReleaseService;
import rocks.metaldetector.butler.facade.dto.ImportJobResultDto;
import rocks.metaldetector.butler.facade.dto.ReleaseDto;
import rocks.metaldetector.config.constants.Endpoints;
import rocks.metaldetector.support.PageRequest;
import rocks.metaldetector.support.TimeRange;
import rocks.metaldetector.web.api.request.PaginatedReleasesRequest;
import rocks.metaldetector.web.api.request.ReleasesRequest;
import rocks.metaldetector.web.api.response.ReleasesResponse;
import rocks.metaldetector.web.transformer.ReleasesResponseTransformer;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static org.springframework.http.HttpStatus.CREATED;

@RestController
@AllArgsConstructor
public class ReleasesRestController {

  private final ReleaseService releaseService;
  private final ReleasesResponseTransformer releasesResponseTransformer;

  @PreAuthorize("hasRole('ROLE_ADMINISTRATOR')")
  @GetMapping(path = Endpoints.Rest.QUERY_ALL_RELEASES,
              produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<List<ReleasesResponse>> findAllReleases(@Valid ReleasesRequest request) {
    var timeRange = new TimeRange(request.getDateFrom(), request.getDateTo());
    List<ReleaseDto> releaseDtos = releaseService.findAllReleases(emptyList(), timeRange);
    List<ReleasesResponse> response = releaseDtos.stream().map(releasesResponseTransformer::transform).collect(Collectors.toList());
    return ResponseEntity.ok(response);
  }

  @GetMapping(path = Endpoints.Rest.QUERY_RELEASES,
              produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<List<ReleasesResponse>> findReleases(@Valid PaginatedReleasesRequest request) {
    var timeRange = new TimeRange(request.getDateFrom(), request.getDateTo());
    var pageRequest = new PageRequest(request.getPage(), request.getSize());
    List<ReleaseDto> releaseDtos = releaseService.findReleases(emptyList(), timeRange, pageRequest);
    List<ReleasesResponse> response = releaseDtos.stream().map(releasesResponseTransformer::transform).collect(Collectors.toList());
    return ResponseEntity.ok(response);
  }

  @PreAuthorize("hasRole('ROLE_ADMINISTRATOR')")
  @PostMapping(path = Endpoints.Rest.IMPORT_JOB)
  public ResponseEntity<Void> createImportJob() {
    releaseService.createImportJob();
    return ResponseEntity.status(CREATED).build();
  }

  @PreAuthorize("hasRole('ROLE_ADMINISTRATOR')")
  @GetMapping(path = Endpoints.Rest.IMPORT_JOB,
              produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<List<ImportJobResultDto>> getImportJobResults() {
    List<ImportJobResultDto> response = releaseService.queryImportJobResults();
    return ResponseEntity.ok(response);
  }

  @PreAuthorize("hasRole('ROLE_ADMINISTRATOR')")
  @PostMapping(path = Endpoints.Rest.COVER_JOB)
  public ResponseEntity<Void> createRetryCoverDownloadJob() {
    releaseService.createRetryCoverDownloadJob();
    return ResponseEntity.ok().build();
  }
}
