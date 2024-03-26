package rocks.metaldetector.persistence.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OAuthAuthorizationStateRepository extends JpaRepository<OAuthAuthorizationStateEntity, Long> {

  Optional<OAuthAuthorizationStateEntity> findByState(String state);
  void deleteByState(String state);
}
