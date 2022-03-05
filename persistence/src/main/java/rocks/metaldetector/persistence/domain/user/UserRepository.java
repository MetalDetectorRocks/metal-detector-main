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

  @Query("select u from users u where dtype = 'native_users' and u.username = :username")
  Optional<AbstractUserEntity> findByUsername(@Param("username") String username);

  Optional<AbstractUserEntity> findByPublicId(String publicId);

  boolean existsByEmail(String email);

  @Query("select case when count(u) > 0 then true else false end from users u where dtype = 'native_users' and u.username = :username")
  boolean existsByUsername(@Param("username") String username);

  // todo NilsD umbauen auf nicht-aktiviert und erstellungsdatum
//  @Query(value = "select * from users as u inner join tokens as t on u.id = t.users_id where t.expiration_date_time < NOW() and t.token_type = 'EMAIL_VERIFICATION'", nativeQuery = true)
  @Query(value = "select * from users as u where u.created_date = u.last_modified_date and u.enabled = false and u.created_date < NOW() - interval '10' day", nativeQuery = true)
  List<AbstractUserEntity> findAllExpiredUsers();
}
