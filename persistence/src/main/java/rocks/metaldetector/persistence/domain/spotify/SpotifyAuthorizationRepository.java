package rocks.metaldetector.persistence.domain.spotify;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rocks.metaldetector.persistence.domain.user.AbstractUserEntity;

import java.util.Optional;

@Repository
public interface SpotifyAuthorizationRepository extends JpaRepository<SpotifyAuthorizationEntity, Long> {

  Optional<SpotifyAuthorizationEntity> findByUser(AbstractUserEntity user);
  void deleteByUser(AbstractUserEntity user);
}
