package com.metalr2.service.followArtist;

import com.metalr2.model.followArtist.FollowedArtistEntity;
import com.metalr2.model.followArtist.FollowedArtistRepository;
import com.metalr2.web.dto.FollowArtistsDto;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FollowArtistServiceImpl implements FollowArtistService {

  private final FollowedArtistRepository followedArtistRepository;
  private final ModelMapper mapper;


  @Autowired
  public FollowArtistServiceImpl(FollowedArtistRepository followedArtistRepository) {
    this.followedArtistRepository = followedArtistRepository;
    this.mapper                   = new ModelMapper();
  }

  @Override
  @Transactional
  public FollowArtistsDto followArtist(FollowArtistsDto followArtistsDto) {
    FollowedArtistEntity followedArtistEntity      = new FollowedArtistEntity(followArtistsDto.getUserId(),followArtistsDto.getArtistDiscogsId());
    FollowedArtistEntity savedFollowedArtistEntity = followedArtistRepository.save(followedArtistEntity);
    return mapper.map(savedFollowedArtistEntity, FollowArtistsDto.class);
  }

  @Override
  @Transactional
  public void unfollowArtist(FollowArtistsDto followArtistsDto) {
    FollowedArtistEntity followedArtistEntity = new FollowedArtistEntity(followArtistsDto.getUserId(),followArtistsDto.getArtistDiscogsId());
    followedArtistRepository.delete(followedArtistEntity);
  }

  @Override
  public boolean userFollowsArtist(FollowArtistsDto followArtistsDto) {
    return false;
  }

  @Override
  public boolean artistFollowedByUser(FollowArtistsDto followArtistsDto) {
    return false;
  }
}
