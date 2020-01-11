package com.metalr2.model.artist;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FollowedArtistsRepository extends JpaRepository<FollowedArtistEntity, Long> {

  List<FollowedArtistEntity> findAllByPublicUserId(String publicUserId);

  boolean existsByPublicUserIdAndArtistDiscogsId(String publicUserId, long artistDiscogsId);

  Optional<FollowedArtistEntity> findByPublicUserIdAndArtistDiscogsId(String publicUserId, long artistDiscogsId);

  List<FollowedArtistEntity> findAllByPublicUserIdAndArtistDiscogsIdIn(String publicUserId, long... artistDiscgogsIds);

  List<FollowedArtistEntity> findAllByPublicUserId(String publicUserId, Pageable pageable);

}
