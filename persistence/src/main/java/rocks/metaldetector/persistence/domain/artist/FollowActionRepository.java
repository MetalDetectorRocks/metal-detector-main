package rocks.metaldetector.persistence.domain.artist;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rocks.metaldetector.persistence.domain.user.UserEntity;

import java.util.List;

@Repository
public interface FollowActionRepository extends JpaRepository<FollowActionEntity, Long> {

  List<FollowActionEntity> findAllByUser(UserEntity user); // ToDo DanielW: Test

  void deleteByUserAndArtist(UserEntity user, ArtistEntity artistEntity); // ToDo DanielW: Test

}
