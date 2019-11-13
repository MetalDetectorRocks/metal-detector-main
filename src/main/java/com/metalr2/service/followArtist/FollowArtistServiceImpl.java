package com.metalr2.service.followArtist;

import com.metalr2.model.followArtist.FollowedArtistEntity;
import com.metalr2.model.followArtist.FollowedArtistsRepository;
import com.metalr2.web.dto.FollowArtistDto;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FollowArtistServiceImpl implements FollowArtistService {

  private final FollowedArtistsRepository followedArtistsRepository;
  private final ModelMapper mapper;

  @Autowired
  public FollowArtistServiceImpl(FollowedArtistsRepository followedArtistsRepository) {
    this.followedArtistsRepository = followedArtistsRepository;
    this.mapper                    = new ModelMapper();
  }

  @Override
  @Transactional
  public FollowArtistDto followArtist(FollowArtistDto followArtistDto) {
    FollowedArtistEntity followedArtistEntity = new FollowedArtistEntity(followArtistDto.getPublicUserId(), followArtistDto.getArtistDiscogsId());
    FollowedArtistEntity savedFollowedArtistEntity = followedArtistsRepository.save(followedArtistEntity);
    return mapper.map(savedFollowedArtistEntity, FollowArtistDto.class);
  }

  @Override
  @Transactional
  public boolean unfollowArtist(FollowArtistDto followArtistDto) {
    boolean entityExists = followedArtistsRepository.existsFollowedArtistEntityByPublicUserIdAndArtistDiscogsId(followArtistDto.getPublicUserId(), followArtistDto.getArtistDiscogsId());

    if (!entityExists){
      return false;
    }

    FollowedArtistEntity followedArtistEntity = new FollowedArtistEntity(followArtistDto.getPublicUserId(), followArtistDto.getArtistDiscogsId());
    followedArtistsRepository.delete(followedArtistEntity);

    return true;
  }

  @Override
  public boolean followArtistEntityExists(FollowArtistDto followArtistDto) {
    return followedArtistsRepository.existsFollowedArtistEntityByPublicUserIdAndArtistDiscogsId(followArtistDto.getPublicUserId(), followArtistDto.getArtistDiscogsId());
  }
}
