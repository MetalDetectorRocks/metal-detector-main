package rocks.metaldetector.persistence.domain.token;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import rocks.metaldetector.persistence.domain.user.AbstractUserEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<TokenEntity, Long> {

  @Query(value = "SELECT * FROM tokens WHERE TOKEN_TYPE = 'EMAIL_VERIFICATION' AND TOKEN_STRING = :tokenString", nativeQuery = true)
  Optional<TokenEntity> findEmailVerificationToken(@Param("tokenString") String tokenString);

  @Query(value = "SELECT * FROM tokens WHERE TOKEN_TYPE = 'PASSWORD_RESET' AND TOKEN_STRING = :tokenString", nativeQuery = true)
  Optional<TokenEntity> findResetPasswordToken(@Param("tokenString") String tokenString);

  @Modifying
  void deleteAllByUserIn(List<AbstractUserEntity> users);
}
