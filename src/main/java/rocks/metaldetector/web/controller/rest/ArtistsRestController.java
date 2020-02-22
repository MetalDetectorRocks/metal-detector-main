package rocks.metaldetector.web.controller.rest;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
import rocks.metaldetector.web.dto.response.ArtistDetailsResponse;
import rocks.metaldetector.web.dto.response.SearchResponse;

import java.util.Optional;

@RestController
@RequestMapping(Endpoints.Rest.ARTISTS)
@AllArgsConstructor
public class ArtistsRestController {

  private final ArtistsService artistsService;

  @GetMapping(path = Endpoints.Rest.SEARCH,
              produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<SearchResponse> handleNameSearch(@RequestParam(value = "query", defaultValue = "") String query,
                                                         @PageableDefault Pageable pageable) {
    Optional<SearchResponse> responseOptional = artistsService.searchDiscogsByName(query, pageable);
    return ResponseEntity.of(responseOptional);
  }

  @GetMapping(path = "/{discogsId}",
              produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<ArtistDetailsResponse> handleDetailsSearchRequest(@PathVariable long discogsId) {
    Optional<ArtistDetailsResponse> responseOptional = artistsService.searchDiscogsById(discogsId);
    return ResponseEntity.of(responseOptional);
  }

  @PostMapping(path = Endpoints.Rest.FOLLOW + "/{discogsId}")
  public ResponseEntity<Void> handleFollow(@PathVariable long discogsId) {
    boolean success = artistsService.followArtist(discogsId);
    return success ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
  }

  @PostMapping(path = Endpoints.Rest.UNFOLLOW + "/{discogsId}")
  public ResponseEntity<Void> handleUnfollow(@PathVariable long discogsId) {
    boolean success = artistsService.unfollowArtist(discogsId);
    return success ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
  }
}
