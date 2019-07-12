package com.metalr2.service.token;

import com.metalr2.model.entities.TokenEntity;

import java.util.Optional;

public interface TokenService {

  Optional<TokenEntity> getResetPasswordTokenByTokenString(String tokenString);

  String createEmailVerificationToken(String userId);

  String createResetPasswordToken(String userId);

  void resendExpiredEmailVerificationToken(String tokenString);

  void deleteToken(TokenEntity tokenEntity);

}
