package rocks.metaldetector.web.controller.rest;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import rocks.metaldetector.config.constants.Endpoints;
import rocks.metaldetector.persistence.domain.artist.ArtistSource;
import rocks.metaldetector.service.artist.ArtistsService;
import rocks.metaldetector.service.artist.FollowArtistService;
import rocks.metaldetector.web.api.response.ArtistSearchResponse;

@RestController
@RequestMapping(Endpoints.Rest.ARTISTS)
@AllArgsConstructor
public class ArtistsRestController {

  static final int DEFAULT_DISCOGS_PAGE = 1;
  static final int DEFAULT_DISCOGS_SIZE = 50;

  private final ArtistsService artistsService;
  private final FollowArtistService followArtistService;

  @GetMapping(path = Endpoints.Rest.SEARCH,
              produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<ArtistSearchResponse> handleNameSearch(@RequestParam(value = "query", defaultValue = "") String query,
                                                               @RequestParam(value = "page", defaultValue = "1") int page,
                                                               @RequestParam(value = "size", defaultValue = "40") int size) {
    ArtistSearchResponse searchResponse = artistsService.searchSpotifyByName(query, PageRequest.of(page, size));

    if (searchResponse.getSearchResults().isEmpty()) {
      searchResponse = artistsService.searchDiscogsByName(query, PageRequest.of(DEFAULT_DISCOGS_PAGE, DEFAULT_DISCOGS_SIZE));
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
