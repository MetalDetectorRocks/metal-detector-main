package rocks.metaldetector.web.controller.rest;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import rocks.metaldetector.butler.facade.ReleaseService;
import rocks.metaldetector.butler.facade.dto.ReleaseDto;
import rocks.metaldetector.service.artist.ArtistDto;
import rocks.metaldetector.service.artist.FollowArtistService;
import rocks.metaldetector.support.DetectorSort;
import rocks.metaldetector.support.Endpoints;
import rocks.metaldetector.support.Page;
import rocks.metaldetector.support.PageRequest;
import rocks.metaldetector.support.TimeRange;
import rocks.metaldetector.web.api.request.PaginatedReleasesRequest;
import rocks.metaldetector.web.api.request.ReleaseUpdateRequest;
import rocks.metaldetector.web.api.request.ReleasesRequest;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@AllArgsConstructor
public class ReleasesRestController {

  private final ReleaseService releaseService;
  private final FollowArtistService followArtistService;

  @PreAuthorize("hasRole('ROLE_ADMINISTRATOR')")
  @GetMapping(path = Endpoints.Rest.ALL_RELEASES,
              produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<List<ReleaseDto>> findAllReleases(@Valid ReleasesRequest request) {
    var timeRange = new TimeRange(request.getDateFrom(), request.getDateTo());
    List<ReleaseDto> releaseDtos = releaseService.findAllReleases(emptyList(), timeRange);
    return ResponseEntity.ok(releaseDtos);
  }

  @GetMapping(path = Endpoints.Rest.RELEASES,
              produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<Page<ReleaseDto>> findReleases(@Valid PaginatedReleasesRequest request) {
    var timeRange = new TimeRange(request.getDateFrom(), request.getDateTo());
    var pageRequest = new PageRequest(request.getPage(), request.getSize(), new DetectorSort(request.getSort(), request.getDirection()));
    Page<ReleaseDto> releasePage = releaseService.findReleases(emptyList(), timeRange, request.getQuery(), pageRequest);
    return ResponseEntity.ok(releasePage);
  }

  @GetMapping(path = Endpoints.Rest.MY_RELEASES,
              produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<Page<ReleaseDto>> findReleasesOfFollowedArtists(@Valid PaginatedReleasesRequest request) {
    var timeRange = new TimeRange(request.getDateFrom(), request.getDateTo());
    var pageRequest = new PageRequest(request.getPage(), request.getSize(), new DetectorSort(request.getSort(), request.getDirection()));
    var followedArtists = followArtistService.getFollowedArtistsOfCurrentUser().stream().map(ArtistDto::getArtistName).collect(Collectors.toList());
    Page<ReleaseDto> releasePage = releaseService.findReleases(followedArtists, timeRange, request.getQuery(), pageRequest);
    return ResponseEntity.ok(releasePage);
  }

  @PreAuthorize("hasRole('ROLE_ADMINISTRATOR')")
  @PutMapping(path = Endpoints.Rest.RELEASES + "/{releaseId}",
              consumes = APPLICATION_JSON_VALUE)
  public ResponseEntity<Void> updateReleaseState(@Valid @RequestBody ReleaseUpdateRequest request, @PathVariable("releaseId") long releaseId) {
    releaseService.updateReleaseState(releaseId, request.getState());
    return ResponseEntity.ok().build();
  }
}
