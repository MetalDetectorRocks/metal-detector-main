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
import rocks.metaldetector.discogs.facade.dto.DiscogsArtistSearchResultDto;
import rocks.metaldetector.service.artist.ArtistsService;
import rocks.metaldetector.service.artist.FollowArtistService;

@RestController
@RequestMapping(Endpoints.Rest.ARTISTS)
@AllArgsConstructor
public class ArtistsRestController {

  private final ArtistsService artistsService;
  private final FollowArtistService followArtistService;

  @GetMapping(path = Endpoints.Rest.SEARCH,
              produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<DiscogsArtistSearchResultDto> handleNameSearch(@RequestParam(value = "query", defaultValue = "") String query,
                                                                       @RequestParam(value = "page", defaultValue = "1") int page,
                                                                       @RequestParam(value = "size", defaultValue = "40") int size) {
    DiscogsArtistSearchResultDto result = artistsService.searchDiscogsByName(query, PageRequest.of(page, size));
    return ResponseEntity.ok(result);
  }

  @PostMapping(path = Endpoints.Rest.FOLLOW + "/{discogsId}")
  public ResponseEntity<Void> handleFollow(@PathVariable long discogsId) {
    followArtistService.follow(discogsId);
    return ResponseEntity.ok().build();
  }

  @PostMapping(path = Endpoints.Rest.UNFOLLOW + "/{discogsId}")
  public ResponseEntity<Void> handleUnfollow(@PathVariable long discogsId) {
    followArtistService.unfollow(discogsId);
    return ResponseEntity.ok().build();
  }
}
