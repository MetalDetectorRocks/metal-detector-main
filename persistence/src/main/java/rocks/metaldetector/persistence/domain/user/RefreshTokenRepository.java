package rocks.metaldetector.persistence.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {
  boolean existsByToken(String token);
  RefreshTokenEntity getByToken(String token);
  void deleteByToken(String token);
}
