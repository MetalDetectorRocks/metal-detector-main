package com.metalr2.web.controller.rest;

import com.metalr2.config.constants.Endpoints;
import com.metalr2.service.artist.ArtistsService;
import com.metalr2.web.dto.request.SearchRequest;
import com.metalr2.web.dto.response.ArtistDetailsResponse;
import com.metalr2.web.dto.response.SearchResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping(Endpoints.Rest.ARTISTS_V1)
public class ArtistsRestController implements Validatable {

  private final ArtistsService artistsService;

  @Autowired
  public ArtistsRestController(ArtistsService artistsService) {
    this.artistsService = artistsService;
  }

  @GetMapping(path = Endpoints.Rest.SEARCH,
              produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<SearchResponse> handleNameSearch(@Valid SearchRequest searchRequest, BindingResult bindingResult) {
    validateRequest(bindingResult);

    Optional<SearchResponse> responseOptional = artistsService.searchDiscogsByName(searchRequest.getQuery(),
                                                                                   searchRequest.getPage(),
                                                                                   searchRequest.getSize());

    if (responseOptional.isEmpty()) {
      return ResponseEntity.notFound().build();
    }

    return ResponseEntity.ok(responseOptional.get());
  }

  @GetMapping(path = "/{discogsId}",
              produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<ArtistDetailsResponse> handleDetailsSearchRequest(@PathVariable long discogsId) {
    Optional<ArtistDetailsResponse> responseOptional = artistsService.searchDiscogsById(discogsId);

    if (responseOptional.isEmpty()) {
      return ResponseEntity.notFound().build();
    }

    ArtistDetailsResponse response = responseOptional.get();
    return ResponseEntity.ok(response);
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
