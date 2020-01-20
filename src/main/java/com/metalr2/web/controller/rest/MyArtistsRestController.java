package com.metalr2.web.controller.rest;

import com.metalr2.config.constants.Endpoints;
import com.metalr2.service.artist.ArtistsService;
import com.metalr2.web.dto.ArtistDto;
import com.metalr2.web.dto.response.MyArtistsResponse;
import com.metalr2.web.dto.response.Pagination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(Endpoints.Rest.MY_ARTISTS)
public class MyArtistsRestController {

  private final ArtistsService artistsService;

  @Autowired
  public MyArtistsRestController(ArtistsService artistsService) {
    this.artistsService = artistsService;
  }

  @GetMapping(produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<MyArtistsResponse> getMyArtists(@PageableDefault Pageable pageable) {
    List<ArtistDto> artists = artistsService.findFollowedArtistsForCurrentUser(PageRequest.of(pageable.getPageNumber()-1, pageable.getPageSize()));
    long totalArtists = artistsService.countFollowedArtistsForCurrentUser();
    Pagination pagination = new Pagination(totalArtists, pageable.getPageNumber(), pageable.getPageSize());
    MyArtistsResponse response = new MyArtistsResponse(artists, pagination);
    return ResponseEntity.ok(response);
  }
}
