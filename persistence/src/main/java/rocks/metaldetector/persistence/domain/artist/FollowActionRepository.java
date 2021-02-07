package rocks.metaldetector.persistence.domain.artist;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rocks.metaldetector.persistence.domain.user.AbstractUserEntity;

import java.util.List;

@Repository
public interface FollowActionRepository extends JpaRepository<FollowActionEntity, Long> {

  List<FollowActionEntity> findAllByUser(AbstractUserEntity user);

  void deleteByUserAndArtist(AbstractUserEntity user, ArtistEntity artistEntity);

  void deleteAllByUser(AbstractUserEntity user);

  boolean existsByUserIdAndArtistId(Long userId, Long artistId);

}
