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
import rocks.metaldetector.service.artist.ArtistsService;
import rocks.metaldetector.service.artist.FollowArtistService;
import rocks.metaldetector.web.api.response.ArtistSearchResponse;

@RestController
@RequestMapping(Endpoints.Rest.ARTISTS)
@AllArgsConstructor
public class ArtistsRestController {

  private final ArtistsService artistsService;
  private final FollowArtistService followArtistService;

  @GetMapping(path = Endpoints.Rest.SEARCH,
              produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<ArtistSearchResponse> handleNameSearch(@RequestParam(value = "query", defaultValue = "") String query,
                                                                       @RequestParam(value = "page", defaultValue = "1") int page,
                                                                       @RequestParam(value = "size", defaultValue = "40") int size) {
    ArtistSearchResponse discogsResult = artistsService.searchDiscogsByName(query, PageRequest.of(page, size));
    ArtistSearchResponse spotifyResult = artistsService.searchSpotifyByName(query, PageRequest.of(page, size)); // ToDo NilsD: for testing only
    return ResponseEntity.ok(spotifyResult);
  }

  @PostMapping(path = Endpoints.Rest.FOLLOW + "/{externalId}")
  public ResponseEntity<Void> handleFollow(@PathVariable String externalId, @RequestParam(value = "source") String source) {
    if (source.isEmpty()) {
      throw new IllegalArgumentException("Artist source must be set");
    }
    followArtistService.follow(externalId, source);
    return ResponseEntity.ok().build();
  }

  @PostMapping(path = Endpoints.Rest.UNFOLLOW + "/{externalId}")
  public ResponseEntity<Void> handleUnfollow(@PathVariable String externalId) {
    followArtistService.unfollow(externalId);
    return ResponseEntity.ok().build();
  }
}
