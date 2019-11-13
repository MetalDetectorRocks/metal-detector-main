package com.metalr2.web.controller;

import com.metalr2.config.constants.Endpoints;
import com.metalr2.model.user.UserEntity;
import com.metalr2.service.followArtist.FollowArtistService;
import com.metalr2.web.dto.FollowArtistDto;
import com.metalr2.web.dto.request.FollowArtistRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(Endpoints.Rest.FOLLOW_ARTISTS_V1)
@Slf4j
public class FollowArtistRestControllerImpl implements FollowArtistRestController<FollowArtistRequest, FollowArtistDto> {

  private final FollowArtistService followArtistService;

  @Autowired
  public FollowArtistRestControllerImpl(FollowArtistService followArtistService){
    this.followArtistService = followArtistService;
  }

  @Override
  @PostMapping(consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<FollowArtistDto> followArtist(@Valid @RequestBody FollowArtistRequest followArtistRequest, BindingResult bindingResult) {
    validateRequest(bindingResult);

    FollowArtistDto followArtistDto = new FollowArtistDto(getPublicUserId(), followArtistRequest.getArtistDiscogsId());
    FollowArtistDto savedFollowArtistDto = followArtistService.followArtist(followArtistDto);

    return ResponseEntity.status(HttpStatus.CREATED).contentType(MediaType.APPLICATION_JSON).body(savedFollowArtistDto);
  }

  @Override
  @DeleteMapping(consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<Void> unfollowArtist(@Valid @RequestBody FollowArtistRequest followArtistRequest, BindingResult bindingResult) {
    validateRequest(bindingResult);

    FollowArtistDto followArtistDto = new FollowArtistDto(getPublicUserId(), followArtistRequest.getArtistDiscogsId());
    boolean success = followArtistService.unfollowArtist(followArtistDto);

    return success ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
  }

  private String getPublicUserId(){
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    return ((UserEntity)auth.getPrincipal()).getPublicId();
  }
}
