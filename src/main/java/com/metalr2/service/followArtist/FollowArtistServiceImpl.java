package com.metalr2.service.followArtist;

import com.metalr2.model.followArtist.FollowedArtistEntity;
import com.metalr2.model.followArtist.FollowedArtistRepository;
import com.metalr2.service.user.UserService;
import com.metalr2.web.dto.UserDto;
import com.metalr2.web.dto.request.FollowArtistRequest;
import org.springframework.beans.factory.annotation.Autowired;
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
  public boolean followArtist(FollowArtistRequest followArtistRequest) {
    Optional<UserDto> userEntityOptional = userService.getUserByEmailOrUsername(followArtistRequest.getEmailOrUserName());

    if (userEntityOptional.isEmpty()) {
      return false;
    }

    UserDto userEntity = userEntityOptional.get();
    FollowedArtistEntity followedArtistEntity = new FollowedArtistEntity(userEntity.getId(),followArtistRequest.getArtistDiscogsId());

    followedArtistRepository.save(followedArtistEntity);

    return true;
  }

  @Override
  @Transactional
  public boolean unfollowArtist(FollowArtistRequest unfollowArtistRequest) {
    return false;
  }
}
