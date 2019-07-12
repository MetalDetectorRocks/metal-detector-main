package com.metalr2.service.token;

import com.metalr2.model.email.RegistrationVerificationEmail;
import com.metalr2.model.entities.TokenEntity;
import com.metalr2.model.entities.TokenType;
import com.metalr2.model.entities.UserEntity;
import com.metalr2.model.exceptions.ErrorMessages;
import com.metalr2.model.exceptions.ResourceNotFoundException;
import com.metalr2.model.repositories.TokenRepository;
import com.metalr2.model.repositories.UserRepository;
import com.metalr2.security.ExpirationTime;
import com.metalr2.service.email.EmailService;
import com.metalr2.utils.JwtsUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Component
@SuppressWarnings("unused") // Spring bean
public class TokenServiceImpl implements TokenService {

  private final TokenRepository tokenRepository;
  private final UserRepository userRepository;
  private final EmailService emailService;

  @Autowired
  public TokenServiceImpl(TokenRepository tokenRepository, UserRepository  userRepository, EmailService emailService) {
    this.tokenRepository = tokenRepository;
    this.userRepository  = userRepository;
    this.emailService    = emailService;
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
    String      tokenString = JwtsUtils.generateToken(userId, expirationTime);
    UserEntity userEntity  = userRepository.findByUserId(userId).orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.USER_WITH_ID_NOT_FOUND.toDisplayString()));
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
    UserEntity userEntity   = tokenEntity.getUser();

    tokenRepository.delete(tokenEntity);
    String newTokenString = createEmailVerificationToken(userEntity.getUserId());
    emailService.sendEmail(new RegistrationVerificationEmail(userEntity.getEmail(), tokenString));
  }

  @Override
  @Transactional
  public void deleteToken(TokenEntity tokenEntity) {
    tokenRepository.delete(tokenEntity);
  }

}
