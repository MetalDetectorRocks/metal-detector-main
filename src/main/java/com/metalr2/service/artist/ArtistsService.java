package com.metalr2.service.artist;

import com.metalr2.web.dto.ArtistDto;
import com.metalr2.web.dto.response.ArtistDetailsResponse;
import com.metalr2.web.dto.response.ArtistNameSearchResponse;
import com.metalr2.web.dto.response.MyArtistsResponse;

import java.util.List;
import java.util.Optional;

public interface ArtistsService {

  Optional<ArtistDto> findArtistByDiscogsId(long discogsId);
  List<ArtistDto> findAllArtistsByDiscogsIds(long... discogsIds);
  boolean existsArtistByDiscogsId(long discogsId);
  boolean fetchAndSaveArtist(long discogsId);

  boolean followArtist(long discogsId);
  boolean unfollowArtist(long discogsId);
  boolean isFollowed(long discogsId);
  MyArtistsResponse findFollowedArtistsPerUser(String publicUserId);
  MyArtistsResponse findFollowedArtistsPerUser(String publicUserId, int page, int size);
  MyArtistsResponse findFollowedArtistsForCurrentUser();
  MyArtistsResponse findFollowedArtistsForCurrentUser(int page, int size);

  Optional<ArtistNameSearchResponse> searchDiscogsByName(String artistQueryString, int page, int size);
  Optional<ArtistDetailsResponse> searchDiscogsById(long discogsId);

}
