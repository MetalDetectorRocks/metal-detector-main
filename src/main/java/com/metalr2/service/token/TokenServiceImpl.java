package com.metalr2.service.token;

import com.metalr2.model.email.RegistrationVerificationEmail;
import com.metalr2.model.exceptions.ErrorMessages;
import com.metalr2.model.exceptions.ResourceNotFoundException;
import com.metalr2.model.token.TokenEntity;
import com.metalr2.model.token.TokenRepository;
import com.metalr2.model.token.TokenType;
import com.metalr2.model.user.UserEntity;
import com.metalr2.model.user.UserRepository;
import com.metalr2.security.ExpirationTime;
import com.metalr2.service.email.EmailService;
import com.metalr2.utils.JwtsSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Component
public class TokenServiceImpl implements TokenService {

  private final TokenRepository tokenRepository;
  private final UserRepository userRepository;
  private final EmailService emailService;
  private final JwtsSupport jwtsSupport;

  @Autowired
  public TokenServiceImpl(TokenRepository tokenRepository, UserRepository  userRepository,
                          EmailService emailService, JwtsSupport jwtsSupport) {
    this.tokenRepository = tokenRepository;
    this.userRepository  = userRepository;
    this.emailService    = emailService;
    this.jwtsSupport     = jwtsSupport;
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<TokenEntity> getResetPasswordTokenByTokenString(String tokenString) {
    return tokenRepository.findResetPasswordToken(tokenString);
  }

  @Override
  @Transactional
  public String createEmailVerificationToken(String userId) {
    return createToken(userId, TokenType.EMAIL_VERIFICATION, ExpirationTime.TEN_DAYS);
  }

  @Override
  @Transactional
  public String createResetPasswordToken(String userId) {
    return createToken(userId, TokenType.PASSWORD_RESET, ExpirationTime.ONE_HOUR);
  }

  private String createToken(String userId, TokenType tokenType, ExpirationTime expirationTime) {
    String      tokenString = jwtsSupport.generateToken(userId, expirationTime);
    UserEntity  userEntity  = userRepository.findByUserId(userId).orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.USER_WITH_ID_NOT_FOUND.toDisplayString()));
    TokenEntity tokenEntity = new TokenEntity();

    tokenEntity.setUser(userEntity);
    tokenEntity.setTokenString(tokenString);
    tokenEntity.setExpirationDateTime(LocalDateTime.now().plus(expirationTime.toMillis(), ChronoUnit.MILLIS));
    tokenEntity.setTokenType(tokenType);

    tokenRepository.save(tokenEntity);

    return tokenString;
  }

  @Override
  @Transactional
  public void resendExpiredEmailVerificationToken(String tokenString) {
    TokenEntity tokenEntity = tokenRepository.findEmailVerificationToken(tokenString)
                                             .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.TOKEN_NOT_FOUND.toDisplayString()));
    UserEntity userEntity = tokenEntity.getUser();

    tokenRepository.delete(tokenEntity);
    String newTokenString = createEmailVerificationToken(userEntity.getUserId());
    emailService.sendEmail(new RegistrationVerificationEmail(userEntity.getEmail(), newTokenString));
  }

  @Override
  @Transactional
  public void deleteToken(TokenEntity tokenEntity) {
    tokenRepository.delete(tokenEntity);
  }

}
