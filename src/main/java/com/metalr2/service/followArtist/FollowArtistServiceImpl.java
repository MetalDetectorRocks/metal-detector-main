 package com.metalr2.service.followArtist;

import com.metalr2.model.followArtist.FollowedArtistEntity;
import com.metalr2.model.followArtist.FollowedArtistsRepository;
import com.metalr2.web.dto.FollowArtistDto;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

 @Service
@Slf4j
public class FollowArtistServiceImpl implements FollowArtistService {

  private final FollowedArtistsRepository followedArtistsRepository;
  private final ModelMapper mapper;

  @Autowired
  public FollowArtistServiceImpl(FollowedArtistsRepository followedArtistsRepository) {
    this.followedArtistsRepository = followedArtistsRepository;
    this.mapper = new ModelMapper();
  }

  @Override
  @Transactional
  public void followArtist(FollowArtistDto followArtistDto) {
    FollowedArtistEntity followedArtistEntity      = new FollowedArtistEntity(followArtistDto.getPublicUserId(),followArtistDto.getArtistDiscogsId());
    FollowedArtistEntity savedFollowedArtistEntity = followedArtistsRepository.save(followedArtistEntity);

    log.debug("User with public id " + savedFollowedArtistEntity.getPublicUserId() + " is now following artist with discogs id " + savedFollowedArtistEntity.getArtistDiscogsId() + ".");
  }

  @Override
  @Transactional
  public boolean unfollowArtist(FollowArtistDto followArtistDto) {
    Optional<FollowedArtistEntity> optionalFollowedArtistEntity = followedArtistsRepository.findByPublicUserIdAndArtistDiscogsId(followArtistDto.getPublicUserId(),followArtistDto.getArtistDiscogsId());

    if (optionalFollowedArtistEntity.isEmpty()){
      return false;
    }

    followedArtistsRepository.delete(optionalFollowedArtistEntity.get());

    log.debug("User with public id " + followArtistDto.getPublicUserId() + " is not following artist with discogs id " + followArtistDto.getArtistDiscogsId() + " anymore.");
    return true;
  }

  @Override
  public boolean exists(FollowArtistDto followArtistDto) {
    return followedArtistsRepository.existsByPublicUserIdAndArtistDiscogsId(followArtistDto.getPublicUserId(), followArtistDto.getArtistDiscogsId());
  }

  @Override
  public List<FollowArtistDto> findFollowedArtistsPerUser(String publicUserId) {
    return followedArtistsRepository.findAllByPublicUserId(publicUserId).stream().map(entity -> mapper.map(entity,FollowArtistDto.class)).collect(Collectors.toList());
  }
}
