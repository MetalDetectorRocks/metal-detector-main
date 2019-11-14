package com.metalr2.model.followArtist;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FollowedArtistsRepository extends JpaRepository<FollowedArtistEntity, Long> {

  List<FollowedArtistEntity> findAllByPublicUserId(String publicUserId);

  List<FollowedArtistEntity> findAllByArtistDiscogsId(long artistDiscogsId);

  boolean existsByPublicUserIdAndArtistDiscogsId(String publicUserId, long artistDiscogsId);

  Optional<FollowedArtistEntity> findByPublicUserIdAndArtistDiscogsId(String publicUserId, long artistDiscogsId);

}
