package rocks.metaldetector.persistence.domain.artist;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import rocks.metaldetector.persistence.domain.user.AbstractUserEntity;

import java.util.List;

@Repository
public interface FollowActionRepository extends JpaRepository<FollowActionEntity, Long> {

  List<FollowActionEntity> findAllByUser(AbstractUserEntity user);

  void deleteByUserAndArtist(AbstractUserEntity user, ArtistEntity artistEntity);

  void deleteAllByUser(AbstractUserEntity user);

  boolean existsByUserAndArtist(AbstractUserEntity user, ArtistEntity artistEntity);

  @Query(value = "select extract(YEAR from fa.created_date) as followingYear, extract(MONTH from fa.created_date) as followingMonth, count(*) as followings from follow_actions fa group by followingYear, followingMonth order by followingYear, followingMonth", nativeQuery = true)
  List<FollowingsPerMonth> groupFollowingsByYearAndMonth();

}
