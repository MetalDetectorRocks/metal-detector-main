package rocks.metaldetector.persistence.domain.artist;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FollowedArtistsRepository extends JpaRepository<FollowedArtistEntity, Long> {

  List<FollowedArtistEntity> findByPublicUserId(String publicUserId);

  boolean existsByPublicUserIdAndDiscogsId(String publicUserId, long discogsId);

  Optional<FollowedArtistEntity> findByPublicUserIdAndDiscogsId(String publicUserId, long discogsId);

  List<FollowedArtistEntity> findByPublicUserId(String publicUserId, Pageable pageable);

  long countByPublicUserId(String publicUserId);

}
