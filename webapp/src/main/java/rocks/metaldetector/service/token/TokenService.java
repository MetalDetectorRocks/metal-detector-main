package rocks.metaldetector.service.token;

import rocks.metaldetector.persistence.domain.token.TokenEntity;

import java.util.Optional;

public interface TokenService {

  Optional<TokenEntity> getResetPasswordTokenByTokenString(String tokenString);

  String createEmailVerificationToken(String publicUserId);

  String createResetPasswordToken(String publicUserId);

  void resendExpiredEmailVerificationToken(String tokenString);

  void deleteToken(TokenEntity tokenEntity);

}
