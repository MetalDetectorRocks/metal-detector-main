package com.metalr2.service.followArtist;

import com.metalr2.model.followArtist.FollowedArtistEntity;
import com.metalr2.model.followArtist.FollowedArtistRepository;
import com.metalr2.model.user.UserEntity;
import com.metalr2.service.user.UserService;
import com.metalr2.web.dto.FollowArtistsDto;
import com.metalr2.web.dto.UserDto;
import com.metalr2.web.dto.request.FollowArtistRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class FollowArtistServiceImpl implements FollowArtistService {

  private final UserService userService;
  private final FollowedArtistRepository followedArtistRepository;

  @Autowired
  public FollowArtistServiceImpl(UserService userService, FollowedArtistRepository followedArtistRepository) {
    this.userService = userService;
    this.followedArtistRepository = followedArtistRepository;
  }

  @Override
  @Transactional
  public boolean followArtist(FollowArtistsDto followArtistsDto) {
    FollowedArtistEntity followedArtistEntity = new FollowedArtistEntity(getUserId(),followArtistsDto.getArtistDiscogsId());

    followedArtistRepository.save(followedArtistEntity);
    return true;
  }

  @Override
  @Transactional
  public void unfollowArtist(FollowArtistsDto followArtistsDto) {
    FollowedArtistEntity followedArtistEntity = new FollowedArtistEntity(getUserId(),followArtistsDto.getArtistDiscogsId());

    followedArtistRepository.delete(followedArtistEntity);
  }

  private long getUserId(){
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();

    Optional<UserDto> userEntityOptional = userService.getUserByEmailOrUsername(((UserEntity)auth.getPrincipal()).getEmail());

    if (userEntityOptional.isEmpty()) {
      throw new IllegalStateException("User not found");
    }

    UserDto userEntity = userEntityOptional.get();

    return userEntity.getId();
  }
}
