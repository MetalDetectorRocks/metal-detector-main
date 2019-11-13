package com.metalr2.model.followArtist;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FollowedArtistsRepository extends JpaRepository<FollowedArtistEntity, Long> {

  List<FollowedArtistEntity> findFollowedArtistEntitiesByPublicUserId(String publicUserId);

  List<FollowedArtistEntity> findFollowedArtistEntitiesByArtistDiscogsId(long artistDiscogsId);

  boolean existsFollowedArtistEntityByPublicUserIdAndArtistDiscogsId(String publicUserId, long artistDiscogsId);

}
