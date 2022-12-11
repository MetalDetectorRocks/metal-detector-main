package rocks.metaldetector.persistence.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<AbstractUserEntity, Long> {

  Optional<AbstractUserEntity> findByEmail(String email);

  @Query(value = "select * from users as u where u.dtype = 'native_users' and u.username = :username", nativeQuery = true)
  Optional<AbstractUserEntity> findByUsername(@Param("username") String username);

  UserEntity getByUsername(String username);

  Optional<AbstractUserEntity> findByPublicId(String publicId);

  boolean existsByEmail(String email);

  @Query(value = "select case when count(*) > 0 then true else false end from users as u where u.dtype = 'native_users' and u.username = :username", nativeQuery = true)
  boolean existsByUsername(@Param("username") String username);

  @Query(value = "select * from users as u where u.created_date = u.last_modified_date and u.enabled = false and u.created_date < NOW() - interval '10' day", nativeQuery = true)
  List<AbstractUserEntity> findAllExpiredUsers();
}
