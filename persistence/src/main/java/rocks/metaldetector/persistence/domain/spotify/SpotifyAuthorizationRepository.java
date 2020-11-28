package rocks.metaldetector.persistence.domain.spotify;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpotifyAuthorizationRepository extends JpaRepository<SpotifyAuthorizationEntity, Long> {

  Optional<SpotifyAuthorizationEntity> findByUserId(Long userId);
}
