package rocks.metaldetector.persistence.domain.artist;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface ArtistRepository extends JpaRepository<ArtistEntity, Long> {

  Optional<ArtistEntity> findByExternalIdAndSource(String externalId, ArtistSource source);

  List<ArtistEntity> findAllByExternalIdIn(Collection<String> externalIds);

  boolean existsByExternalIdAndSource(String externalId, ArtistSource source);

}
