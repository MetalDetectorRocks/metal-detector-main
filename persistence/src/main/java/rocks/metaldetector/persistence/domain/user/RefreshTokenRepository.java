package rocks.metaldetector.persistence.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {
  boolean existsByToken(String token);
  RefreshTokenEntity getByToken(String token);
  void deleteByToken(String token);
  List<RefreshTokenEntity> findAllByLastModifiedDateTimeBefore(Date date);
}
