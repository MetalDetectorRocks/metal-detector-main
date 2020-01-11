package com.metalr2.web.controller.rest;

import com.metalr2.config.constants.Endpoints;
import com.metalr2.service.artist.ArtistsService;
import com.metalr2.web.dto.response.MyArtistsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Endpoints.Rest.MY_ARTISTS)
public class MyArtistsRestController {

  private final ArtistsService artistsService;

  @Autowired
  public MyArtistsRestController(ArtistsService artistsService) {
    this.artistsService = artistsService;
  }

  @GetMapping(produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<MyArtistsResponse> getMyArtists(@PageableDefault(page = 0, size = 10) Pageable pageable) {
    MyArtistsResponse response = artistsService.findFollowedArtistsForCurrentUser(pageable);
    return ResponseEntity.ok(response);
  }

}
