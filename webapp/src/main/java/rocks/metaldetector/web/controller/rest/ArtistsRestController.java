package rocks.metaldetector.web.controller.rest;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import rocks.metaldetector.persistence.domain.artist.ArtistSource;
import rocks.metaldetector.service.artist.ArtistDto;
import rocks.metaldetector.service.artist.ArtistSearchService;
import rocks.metaldetector.service.artist.FollowArtistService;
import rocks.metaldetector.service.dashboard.ArtistCollector;
import rocks.metaldetector.web.api.response.ArtistSearchResponse;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static rocks.metaldetector.support.Endpoints.Rest.FOLLOW_ARTIST;
import static rocks.metaldetector.support.Endpoints.Rest.SEARCH_ARTIST;
import static rocks.metaldetector.support.Endpoints.Rest.TOP_ARTISTS;
import static rocks.metaldetector.support.Endpoints.Rest.UNFOLLOW_ARTIST;

@RestController
@AllArgsConstructor
public class ArtistsRestController {

  static final int DEFAULT_DISCOGS_PAGE = 1;
  static final int DEFAULT_DISCOGS_SIZE = 50;

  private final ArtistSearchService artistSearchService;
  private final FollowArtistService followArtistService;
  private final ArtistCollector artistCollector;

  @GetMapping(path = SEARCH_ARTIST, produces = APPLICATION_JSON_VALUE)
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

  @PostMapping(path = FOLLOW_ARTIST + "/{source}/{externalId}")
  public ResponseEntity<Void> handleFollow(@PathVariable ArtistSource source, @PathVariable String externalId) {
    followArtistService.follow(externalId, source);
    return ResponseEntity.ok().build();
  }

  @PostMapping(path = UNFOLLOW_ARTIST + "/{source}/{externalId}")
  public ResponseEntity<Void> handleUnfollow(@PathVariable ArtistSource source, @PathVariable String externalId) {
    followArtistService.unfollow(externalId, source);
    return ResponseEntity.ok().build();
  }

  @GetMapping(path = TOP_ARTISTS, produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<List<ArtistDto>> fetchTopArtists(@RequestParam(required = false, defaultValue = "2") int minFollower,
                                                         @RequestParam(required = false, defaultValue = "10") int limit) {
    var topArtists = artistCollector.collectTopFollowedArtists(minFollower)
        .stream()
        .limit(limit)
        .collect(Collectors.toList());
    return ResponseEntity.ok(topArtists);
  }
}
