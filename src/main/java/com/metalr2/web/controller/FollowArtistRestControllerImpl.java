package com.metalr2.web.controller;

import com.metalr2.config.constants.Endpoints;
import com.metalr2.model.user.UserEntity;
import com.metalr2.service.followArtist.FollowArtistService;
import com.metalr2.service.user.UserService;
import com.metalr2.web.dto.FollowArtistDto;
import com.metalr2.web.dto.UserDto;
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
import java.util.Optional;

@RestController
@RequestMapping(Endpoints.Rest.FOLLOW_ARTISTS_V1)
@Slf4j
public class FollowArtistRestControllerImpl implements FollowArtistRestController<FollowArtistRequest, FollowArtistDto> {

  private final UserService userService;
  private final FollowArtistService followArtistService;

  @Autowired
  public FollowArtistRestControllerImpl(UserService userService, FollowArtistService followArtistService){
    this.userService         = userService;
    this.followArtistService = followArtistService;
  }

  @Override
  @PostMapping(consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<FollowArtistDto> followArtist(@Valid @RequestBody FollowArtistRequest followArtistRequest, BindingResult bindingResult) {
    validateRequest(bindingResult);

    FollowArtistDto followArtistDto = new FollowArtistDto(getUserId(), followArtistRequest.getArtistDiscogsId());
    FollowArtistDto savedFollowArtistDto = followArtistService.followArtist(followArtistDto);
    return ResponseEntity.status(HttpStatus.CREATED).body(savedFollowArtistDto);
  }

  @Override
  @DeleteMapping(consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
  public void unfollowArtist(@RequestBody FollowArtistRequest followArtistRequest, BindingResult bindingResult) {
    validateRequest(bindingResult);

    FollowArtistDto followArtistDto = new FollowArtistDto(getUserId(), followArtistRequest.getArtistDiscogsId());
    followArtistService.unfollowArtist(followArtistDto);
  }

  private long getUserId(){
    Authentication auth                  = SecurityContextHolder.getContext().getAuthentication();
    Optional<UserDto> userEntityOptional = userService.getUserByEmailOrUsername(((UserEntity)auth.getPrincipal()).getEmail());

    if (userEntityOptional.isEmpty()) {
      throw new IllegalStateException("User not found"); // TODO: 06.11.19 better ways than exception?
    }

    UserDto userEntity = userEntityOptional.get();
    return userEntity.getId();
  }
}
