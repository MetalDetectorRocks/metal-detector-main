package rocks.metaldetector.web.controller.rest;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import rocks.metaldetector.service.artist.ArtistDto;
import rocks.metaldetector.service.artist.FollowArtistService;
import rocks.metaldetector.web.api.response.MyArtistsResponse;
import rocks.metaldetector.web.transformer.MyArtistsResponseTransformer;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static rocks.metaldetector.support.Endpoints.Rest.MY_ARTISTS;

@RestController
@AllArgsConstructor
public class MyArtistsRestController {

  private final FollowArtistService followArtistService;
  private final MyArtistsResponseTransformer responseTransformer;

  @GetMapping(path = MY_ARTISTS, produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<MyArtistsResponse> getMyArtists(@RequestParam(value = "page", defaultValue = "1") int page,
                                                        @RequestParam(value = "size", defaultValue = "20") int size) {
    List<ArtistDto> followedArtists = followArtistService.getFollowedArtistsOfCurrentUser();
    MyArtistsResponse response = responseTransformer.transform(followedArtists, page, size);
    return ResponseEntity.ok(response);
  }
}
