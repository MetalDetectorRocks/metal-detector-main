package com.metalr2.web.controller.rest;

import com.metalr2.config.constants.Endpoints;
import com.metalr2.security.CurrentUserSupplier;
import com.metalr2.service.artist.FollowArtistService;
import com.metalr2.web.dto.FollowArtistDto;
import com.metalr2.web.dto.request.FollowArtistRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(Endpoints.Rest.FOLLOW_ARTISTS_V1)
public class FollowArtistRestController implements Validatable {

  private final FollowArtistService followArtistService;
  private final CurrentUserSupplier currentUserSupplier;

  @Autowired
  public FollowArtistRestController(FollowArtistService followArtistService, CurrentUserSupplier currentUserSupplier){
    this.followArtistService = followArtistService;
    this.currentUserSupplier = currentUserSupplier;
  }

  @PostMapping(consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<Void> followArtist(@Valid @RequestBody FollowArtistRequest followArtistRequest, BindingResult bindingResult) {
    validateRequest(bindingResult);

    FollowArtistDto followArtistDto = FollowArtistDto.builder()
            .publicUserId(currentUserSupplier.get().getPublicId())
            .artistDiscogsId(followArtistRequest.getArtistDiscogsId())
            .artistName(followArtistRequest.getArtistName())
            .build();
    followArtistService.followArtist(followArtistDto);

    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @DeleteMapping(consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<Void> unfollowArtist(@Valid @RequestBody FollowArtistRequest followArtistRequest, BindingResult bindingResult) {
    validateRequest(bindingResult);

    FollowArtistDto followArtistDto = FollowArtistDto.builder()
            .publicUserId(currentUserSupplier.get().getPublicId())
            .artistDiscogsId(followArtistRequest.getArtistDiscogsId())
            .artistName(followArtistRequest.getArtistName())
            .build();
    boolean success = followArtistService.unfollowArtist(followArtistDto);

    return success ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
  }

}
