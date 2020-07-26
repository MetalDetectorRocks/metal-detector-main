package rocks.metaldetector.web.controller.rest;

import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import rocks.metaldetector.config.constants.Endpoints;
import rocks.metaldetector.service.SlicingService;
import rocks.metaldetector.service.artist.ArtistDto;
import rocks.metaldetector.service.artist.FollowArtistService;
import rocks.metaldetector.support.Pagination;
import rocks.metaldetector.web.api.response.MyArtistsResponse;

import java.util.List;

@RestController
@RequestMapping(Endpoints.Rest.MY_ARTISTS)
@AllArgsConstructor
public class MyArtistsRestController {

  private final FollowArtistService followArtistService;
  private final SlicingService slicingService;

  @GetMapping(produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<MyArtistsResponse> getMyArtists(@RequestParam(value = "page", defaultValue = "1") int page,
                                                        @RequestParam(value = "size", defaultValue = "20") int size) {
    List<ArtistDto> followedArtists = followArtistService.getFollowedArtistsOfCurrentUser();

    int totalPages = followedArtists.size() % size == 0 ? followedArtists.size() / size : followedArtists.size() / size + 1;
    Pagination pagination = new Pagination(totalPages, page, size);
    MyArtistsResponse response = new MyArtistsResponse(slicingService.slice(followedArtists, page, size), pagination);
    return ResponseEntity.ok(response);
  }
}
