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

  @Query(value = "select a.artist_name as artistName, a.thumb as thumb, a.external_id as externalId " +
                 "from artists as a left join follow_actions as fa on a.id = fa.artist_id " +
                 "group by a.artist_name, a.thumb, a.external_id " +
                 "order by count(a.id) desc " +
                 "limit :limit",
         nativeQuery = true)
  List<TopArtist> findTopArtists(@Param("limit") int limit);

  @Query(value = "select count(a.external_id) " +
                 "from artists a left join follow_actions fa on a.id = fa.artist_id " +
                 "where a.external_id = :externalId",
         nativeQuery = true)
  int countArtistFollower(@Param("externalId") String externalId);
}
