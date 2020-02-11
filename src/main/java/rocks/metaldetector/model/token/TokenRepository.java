package rocks.metaldetector.model.token;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<TokenEntity, Long> {

  @Query(value = "SELECT * FROM tokens WHERE TOKEN_TYPE = 'EMAIL_VERIFICATION' AND TOKEN_STRING = :tokenString", nativeQuery = true)
  Optional<TokenEntity> findEmailVerificationToken(String tokenString);

  @Query(value = "SELECT * FROM tokens WHERE TOKEN_TYPE = 'PASSWORD_RESET' AND TOKEN_STRING = :tokenString", nativeQuery = true)
  Optional<TokenEntity> findResetPasswordToken(String tokenString);

}
