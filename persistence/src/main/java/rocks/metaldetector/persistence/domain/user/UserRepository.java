package rocks.metaldetector.persistence.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<AbstractUserEntity, Long> {

  Optional<AbstractUserEntity> findByEmail(String email);

  @Query(value = "select * from users as u where u.dtype = 'native_users' and u.username = :username", nativeQuery = true)
  Optional<AbstractUserEntity> findByUsername(@Param("username") String username);

  Optional<AbstractUserEntity> findByPublicId(String publicId);

  boolean existsByEmail(String email);

  boolean existsByUsername(String username);
}
