package com.metalr2.service.artist;

import com.metalr2.web.dto.ArtistDto;
import com.metalr2.web.dto.FollowArtistDto;
import com.metalr2.web.dto.response.ArtistDetailsResponse;
import com.metalr2.web.dto.response.ArtistNameSearchResponse;

import java.util.List;
import java.util.Optional;

public interface ArtistsService {

  Optional<ArtistDto> findArtistByDiscogsId(long discogsId);
  List<ArtistDto> findAllArtistsByDiscogsIds(long... discogsIds);
  boolean existsArtistByDiscogsId(long discogsId);

  boolean followArtist(long discogsId);
  boolean unfollowArtist(long discogsId);
  boolean isFollowed(long discogsId);
  List<FollowArtistDto> findFollowedArtistsPerUser(String publicUserId);

  Optional<ArtistNameSearchResponse> searchDiscogsByName(String artistQueryString, int page, int size);
  Optional<ArtistDetailsResponse> searchDiscogsById(long artistId);

}
