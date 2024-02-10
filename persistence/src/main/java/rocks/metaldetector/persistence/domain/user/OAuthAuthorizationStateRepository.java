package rocks.metaldetector.persistence.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OAuthAuthorizationStateRepository extends JpaRepository<OAuthAuthorizationStateEntity, Long> {

  OAuthAuthorizationStateEntity findByState(String state);
  void deleteByState(String state);
}
