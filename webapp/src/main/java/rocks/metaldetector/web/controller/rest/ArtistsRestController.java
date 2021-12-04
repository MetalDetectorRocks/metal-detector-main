package rocks.metaldetector.web.controller.rest;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import rocks.metaldetector.persistence.domain.artist.ArtistSource;
import rocks.metaldetector.service.artist.ArtistSearchService;
import rocks.metaldetector.service.artist.FollowArtistService;
import rocks.metaldetector.support.Endpoints;
import rocks.metaldetector.web.api.response.ArtistSearchResponse;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@AllArgsConstructor
public class ArtistsRestController {

  static final int DEFAULT_DISCOGS_PAGE = 1;
  static final int DEFAULT_DISCOGS_SIZE = 50;

  private final ArtistSearchService artistSearchService;
  private final FollowArtistService followArtistService;

  @GetMapping(path = Endpoints.Rest.SEARCH,
              produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<ArtistSearchResponse> handleNameSearch(@RequestParam(value = "query", defaultValue = "") String query,
                                                               @RequestParam(value = "page", defaultValue = "1") int page,
                                                               @RequestParam(value = "size", defaultValue = "40") int size) {
    if (query.isBlank()) {
      return ResponseEntity.ok(ArtistSearchResponse.empty());
    }

    ArtistSearchResponse searchResponse = artistSearchService.searchSpotifyByName(query, PageRequest.of(page, size));

    if (page == 1 && searchResponse.getSearchResults().isEmpty()) {
      searchResponse = artistSearchService.searchDiscogsByName(query, PageRequest.of(DEFAULT_DISCOGS_PAGE, DEFAULT_DISCOGS_SIZE));
    }

    return ResponseEntity.ok(searchResponse);
  }

  @PostMapping(path = Endpoints.Rest.FOLLOW + "/{source}/{externalId}")
  public ResponseEntity<Void> handleFollow(@PathVariable String source, @PathVariable String externalId) {
    followArtistService.follow(externalId, ArtistSource.getArtistSourceFromString(source));
    return ResponseEntity.ok().build();
  }

  @PostMapping(path = Endpoints.Rest.UNFOLLOW + "/{source}/{externalId}")
  public ResponseEntity<Void> handleUnfollow(@PathVariable String source, @PathVariable String externalId) {
    followArtistService.unfollow(externalId, ArtistSource.getArtistSourceFromString(source));
    return ResponseEntity.ok().build();
  }
}
