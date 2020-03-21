package rocks.metaldetector.web.controller.rest;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rocks.metaldetector.config.constants.Endpoints;
import rocks.metaldetector.service.artist.ArtistsService;
import rocks.metaldetector.service.artist.ArtistDto;
import rocks.metaldetector.web.api.response.MyArtistsResponse;
import rocks.metaldetector.support.Pagination;

import java.util.List;

@RestController
@RequestMapping(Endpoints.Rest.MY_ARTISTS)
@AllArgsConstructor
public class MyArtistsRestController {

  private final ArtistsService artistsService;

  @GetMapping(produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<MyArtistsResponse> getMyArtists(@PageableDefault Pageable pageable) {
    List<ArtistDto> artists = artistsService.findFollowedArtistsForCurrentUser(pageable);
    long totalArtists = artistsService.countFollowedArtistsForCurrentUser();
    Pagination pagination = new Pagination(totalArtists, pageable.getPageNumber(), pageable.getPageSize());
    MyArtistsResponse response = new MyArtistsResponse(artists, pagination);
    return ResponseEntity.ok(response);
  }
}
