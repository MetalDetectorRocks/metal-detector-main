package com.metalr2.model.artist;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArtistsRepository extends JpaRepository<ArtistEntity, Long> {

  Optional<ArtistEntity> findByArtistDiscogsId(long artistDiscogsId);

  @Query(value = "SELECT * FROM artists WHERE ARTIST_DISCOGS_ID in (:artistDiscogsIds)", nativeQuery = true)
  List<ArtistEntity> findAllByArtistDiscogsIds(long... artistDiscogsIds);

  boolean existsByArtistDiscogsId(long artistDiscogsId);

}
