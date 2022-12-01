package rocks.metaldetector.web.controller.rest;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import rocks.metaldetector.butler.facade.ReleaseService;
import rocks.metaldetector.butler.facade.dto.ReleaseDto;
import rocks.metaldetector.service.artist.ArtistDto;
import rocks.metaldetector.service.artist.FollowArtistService;
import rocks.metaldetector.service.dashboard.ArtistCollector;
import rocks.metaldetector.service.dashboard.ReleaseCollector;
import rocks.metaldetector.support.DetectorSort;
import rocks.metaldetector.support.Page;
import rocks.metaldetector.support.PageRequest;
import rocks.metaldetector.support.TimeRange;
import rocks.metaldetector.web.api.request.PaginatedReleasesRequest;
import rocks.metaldetector.web.api.request.ReleaseUpdateRequest;
import rocks.metaldetector.web.api.request.ReleasesRequest;

import java.time.LocalDate;
import java.util.List;

import static java.util.Collections.emptyList;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static rocks.metaldetector.support.Endpoints.Rest.ALL_RELEASES;
import static rocks.metaldetector.support.Endpoints.Rest.RELEASES;
import static rocks.metaldetector.support.Endpoints.Rest.TOP_UPCOMING_RELEASES;

@RestController
@AllArgsConstructor
public class ReleasesRestController {

  private final ReleaseService releaseService;
  private final FollowArtistService followArtistService;
  private final ArtistCollector artistCollector;
  private final ReleaseCollector releaseCollector;

  @PreAuthorize("hasRole('ROLE_ADMINISTRATOR')")
  @GetMapping(path = ALL_RELEASES, produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<List<ReleaseDto>> findAllReleases(@Valid ReleasesRequest request) {
    var timeRange = new TimeRange(request.getDateFrom(), request.getDateTo());
    List<ReleaseDto> releaseDtos = releaseService.findAllReleases(emptyList(), timeRange);
    return ResponseEntity.ok(releaseDtos);
  }

  @GetMapping(path = RELEASES, produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<Page<ReleaseDto>> findReleases(@Valid PaginatedReleasesRequest request) {
    var timeRange = new TimeRange(request.getDateFrom(), request.getDateTo());
    var pageRequest = new PageRequest(request.getPage(), request.getSize(), new DetectorSort(request.getSort(), request.getDirection()));

    List<String> artistNames = emptyList();
    if (request.getReleasesFilter().equalsIgnoreCase("my")) {
      artistNames = followArtistService.getFollowedArtistsOfCurrentUser().stream().map(ArtistDto::getArtistName).toList();
      if (artistNames.isEmpty()) {
        return ResponseEntity.ok(Page.empty());
      }
    }

    Page<ReleaseDto> releasePage = releaseService.findReleases(artistNames, timeRange, request.getQuery(), pageRequest);
    return ResponseEntity.ok(releasePage);
  }

  @PreAuthorize("hasRole('ROLE_ADMINISTRATOR')")
  @PutMapping(path = RELEASES + "/{releaseId}", consumes = APPLICATION_JSON_VALUE)
  public ResponseEntity<Void> updateReleaseState(@Valid @RequestBody ReleaseUpdateRequest request, @PathVariable("releaseId") long releaseId) {
    releaseService.updateReleaseState(releaseId, request.getState());
    return ResponseEntity.ok().build();
  }

  @GetMapping(path = TOP_UPCOMING_RELEASES, produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<List<ReleaseDto>> fetchTopUpcomingReleases(@RequestParam(required = false, defaultValue = "2") int minFollower,
                                                                   @RequestParam(required = false, defaultValue = "10") int limit) {
    var fromDate = LocalDate.now();
    var toDate = fromDate.plusMonths(6);
    var topFollowedArtists = artistCollector.collectTopFollowedArtists(minFollower);
    var topReleases = releaseCollector.collectTopReleases(new TimeRange(fromDate, toDate), topFollowedArtists, limit);
    return ResponseEntity.ok(topReleases);
  }
}
