package rocks.metaldetector.persistence.domain.artist;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface ArtistRepository extends JpaRepository<ArtistEntity, Long> {

  Optional<ArtistEntity> findByExternalIdAndSource(String externalId, ArtistSource source);

  List<ArtistEntity> findAllByExternalIdIn(Collection<String> externalIds);

  boolean existsByExternalIdAndSource(String externalId, ArtistSource source);

  @Query(value = "select a.id, a.created_by, a.created_date, a.last_modified_by, a.last_modified_date, a.artist_name, a.external_id, a.source, a.thumb " +
                 "from artists a left join users_followed_artists ufa on a.id = ufa.artist_id " +
                 "group by a.id, a.created_by, a.created_date, a.last_modified_by, a.last_modified_date, a.artist_name, a.external_id, a.source, a.thumb " +
                 "order by count(a.id) desc " +
                 "limit :limit",
         nativeQuery = true)
  List<ArtistEntity> findTopArtists(@Param("limit") int limit);

  @Query(value = "select count(a.external_id) " +
                 "from artists a left join users_followed_artists ufa on a.id = ufa.artist_id " +
                 "where a.external_id = :externalId",
         nativeQuery = true)
  int countArtistFollower(@Param("externalId") String externalId);
}
