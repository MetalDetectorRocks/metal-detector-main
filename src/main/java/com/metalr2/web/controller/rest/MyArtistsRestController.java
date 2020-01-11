package com.metalr2.web.controller.rest;

import com.metalr2.config.constants.Endpoints;
import com.metalr2.service.artist.ArtistsService;
import com.metalr2.web.dto.response.MyArtistsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Endpoints.Rest.MY_ARTISTS)
public class MyArtistsRestController {

  private static final String DEFAULT_PAGE = "1";
  private static final String DEFAULT_SIZE = "1";

  private final ArtistsService artistsService;

  @Autowired
  public MyArtistsRestController(ArtistsService artistsService) {
    this.artistsService = artistsService;
  }

  @GetMapping(produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<MyArtistsResponse> getMyArtists(@RequestParam(value = "page", defaultValue = DEFAULT_PAGE) int page,
                                                        @RequestParam(value = "size", defaultValue = DEFAULT_SIZE) int size) {
    MyArtistsResponse response = artistsService.findFollowedArtistsForCurrentUser(page, size);
    return ResponseEntity.ok(response);
  }

}
