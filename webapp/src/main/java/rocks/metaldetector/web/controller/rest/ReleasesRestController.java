package rocks.metaldetector.web.controller.rest;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import rocks.metaldetector.butler.facade.ReleaseService;
import rocks.metaldetector.butler.facade.dto.ImportJobResultDto;
import rocks.metaldetector.butler.facade.dto.ReleaseDto;
import rocks.metaldetector.service.artist.ArtistDto;
import rocks.metaldetector.service.artist.FollowArtistService;
import rocks.metaldetector.support.Endpoints;
import rocks.metaldetector.support.Page;
import rocks.metaldetector.support.PageRequest;
import rocks.metaldetector.support.TimeRange;
import rocks.metaldetector.web.api.request.PaginatedReleasesRequest;
import rocks.metaldetector.web.api.request.ReleaseUpdateRequest;
import rocks.metaldetector.web.api.request.ReleasesRequest;
import rocks.metaldetector.web.transformer.SortingTransformer;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_XML_VALUE;

@RestController
@AllArgsConstructor
public class ReleasesRestController {

  private final ReleaseService releaseService;
  private final FollowArtistService followArtistService;
  private final SortingTransformer sortingTransformer;

  @PreAuthorize("hasRole('ROLE_ADMINISTRATOR')")
  @GetMapping(path = Endpoints.Rest.ALL_RELEASES,
              produces = {APPLICATION_XML_VALUE, APPLICATION_JSON_VALUE})
  public ResponseEntity<List<ReleaseDto>> findAllReleases(@Valid ReleasesRequest request) {
    var timeRange = new TimeRange(request.getDateFrom(), request.getDateTo());
    List<ReleaseDto> releaseDtos = releaseService.findAllReleases(emptyList(), timeRange);
    return ResponseEntity.ok(releaseDtos);
  }

  @GetMapping(path = Endpoints.Rest.RELEASES,
              produces = {APPLICATION_XML_VALUE, APPLICATION_JSON_VALUE})
  public ResponseEntity<Page<ReleaseDto>> findReleases(@Valid PaginatedReleasesRequest request,
                                                       @SortDefault(sort = {"releaseDate", "artist", "albumTitle"}, direction=ASC) Sort sort) {
    var timeRange = new TimeRange(request.getDateFrom(), request.getDateTo());
    var sorting = sortingTransformer.transform(sort);
    var pageRequest = new PageRequest(request.getPage(), request.getSize(), sorting);
    Page<ReleaseDto> releasePage = releaseService.findReleases(emptyList(), timeRange, pageRequest);
    return ResponseEntity.ok(releasePage);
  }

  @GetMapping(path = Endpoints.Rest.MY_RELEASES,
              produces = {APPLICATION_XML_VALUE, APPLICATION_JSON_VALUE})
  public ResponseEntity<Page<ReleaseDto>> findReleasesOfFollowedArtists(@Valid PaginatedReleasesRequest request,
                                                                        @SortDefault(sort = {"releaseDate", "artist", "albumTitle"}, direction=ASC) Sort sort) {
    var timeRange = new TimeRange(request.getDateFrom(), request.getDateTo());
    var sorting = sortingTransformer.transform(sort);
    var pageRequest = new PageRequest(request.getPage(), request.getSize(), sorting);
    var followedArtists = followArtistService.getFollowedArtistsOfCurrentUser().stream().map(ArtistDto::getArtistName).collect(Collectors.toList());
    Page<ReleaseDto> releasePage = releaseService.findReleases(followedArtists, timeRange, pageRequest);
    return ResponseEntity.ok(releasePage);
  }

  @PreAuthorize("hasRole('ROLE_ADMINISTRATOR')")
  @PostMapping(path = Endpoints.Rest.IMPORT_JOB)
  public ResponseEntity<Void> createImportJob() {
    releaseService.createImportJob();
    return ResponseEntity.status(CREATED).build();
  }

  @PreAuthorize("hasRole('ROLE_ADMINISTRATOR')")
  @GetMapping(path = Endpoints.Rest.IMPORT_JOB,
              produces = {APPLICATION_XML_VALUE, APPLICATION_JSON_VALUE})
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

  @PreAuthorize("hasRole('ROLE_ADMINISTRATOR')")
  @PutMapping(path = Endpoints.Rest.RELEASES + "/{releaseId}",
              consumes = {APPLICATION_XML_VALUE, APPLICATION_JSON_VALUE})
  public ResponseEntity<Void> updateReleaseState(@Valid @RequestBody ReleaseUpdateRequest request, @PathVariable("releaseId") long releaseId) {
    releaseService.updateReleaseState(releaseId, request.getState());
    return ResponseEntity.ok().build();
  }
}
