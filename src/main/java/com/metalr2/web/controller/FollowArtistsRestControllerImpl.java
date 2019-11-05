package com.metalr2.web.controller;

import com.metalr2.config.constants.Endpoints;
import com.metalr2.model.user.UserEntity;
import com.metalr2.service.followArtist.FollowArtistService;
import com.metalr2.web.dto.FollowArtistsDto;
import com.metalr2.web.dto.request.FollowArtistRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
@RestController
@RequestMapping(Endpoints.Rest.FOLLOW_ARTISTS_V1)
@Slf4j
public class FollowArtistsRestControllerImpl implements FollowArtistsRestController<FollowArtistRequest> {

  private final FollowArtistService followArtistService;

  @Autowired
  public FollowArtistsRestControllerImpl(FollowArtistService followArtistService){
    this.followArtistService = followArtistService;
  }

  @Override
  @PostMapping(consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
  public boolean followArtist(@RequestBody FollowArtistRequest followArtistRequest) {
    FollowArtistsDto followArtistsDto = new FollowArtistsDto(followArtistRequest.getArtistDiscogsId());
    return followArtistService.followArtist(followArtistsDto);
  }

  @Override
  @DeleteMapping(consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
  public void unfollowArtist(@RequestBody FollowArtistRequest followArtistRequest) {
    FollowArtistsDto followArtistsDto = new FollowArtistsDto(followArtistRequest.getArtistDiscogsId());
    followArtistService.unfollowArtist(followArtistsDto);
  }
}
