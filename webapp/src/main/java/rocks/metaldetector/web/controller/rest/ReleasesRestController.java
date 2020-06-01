package rocks.metaldetector.web.controller.rest;

import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import rocks.metaldetector.butler.facade.ReleaseService;
import rocks.metaldetector.butler.facade.dto.ImportJobResultDto;
import rocks.metaldetector.butler.facade.dto.ReleaseDto;
import rocks.metaldetector.config.constants.Endpoints;
import rocks.metaldetector.web.api.request.DetectorReleasesRequest;
import rocks.metaldetector.web.api.response.DetectorImportJobResponse;
import rocks.metaldetector.web.api.response.DetectorReleasesResponse;
import rocks.metaldetector.web.transformer.DetectorImportJobResponseTransformer;
import rocks.metaldetector.web.transformer.DetectorReleasesResponseTransformer;

import javax.validation.Valid;
import java.util.List;

@RestController
@AllArgsConstructor
public class ReleasesRestController {

  private final ReleaseService releaseService;
  private final DetectorReleasesResponseTransformer releasesResponseTransformer;
  private final DetectorImportJobResponseTransformer importJobResponseTransformer;

  @PostMapping(path = Endpoints.Rest.QUERY_RELEASES,
               consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE},
               produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<List<DetectorReleasesResponse>> getReleases(@Valid @RequestBody DetectorReleasesRequest request) {
    List<ReleaseDto> releaseDtos = releaseService.findReleases(request.getArtists(), request.getDateFrom(), request.getDateTo());
    List<DetectorReleasesResponse> response = releasesResponseTransformer.transformListOf(releaseDtos);
    return ResponseEntity.ok(response);
  }

  @GetMapping(path = Endpoints.Rest.CREATE_IMPORT_JOB,
              produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<DetectorImportJobResponse> createImportJob() {
    ImportJobResultDto importResult = releaseService.createImportJob();
    DetectorImportJobResponse response = importJobResponseTransformer.transform(importResult);
    return ResponseEntity.ok(response);
  }
}
