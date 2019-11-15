package com.metalr2.service.followArtist;

import com.metalr2.web.dto.FollowArtistDto;

import java.util.List;

public interface FollowArtistService {

  void followArtist(FollowArtistDto followArtistDto);

  boolean unfollowArtist(FollowArtistDto followArtistDto);

  boolean exists(FollowArtistDto followArtistDto);

  List<FollowArtistDto> findPerUser(String publicUserId);

}
