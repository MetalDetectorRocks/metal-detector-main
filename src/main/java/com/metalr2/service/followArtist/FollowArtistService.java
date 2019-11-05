package com.metalr2.service.followArtist;

import com.metalr2.web.dto.FollowArtistsDto;

public interface FollowArtistService {

  boolean followArtist(FollowArtistsDto followArtistsDto);

  void unfollowArtist(FollowArtistsDto followArtistsDto);

}
