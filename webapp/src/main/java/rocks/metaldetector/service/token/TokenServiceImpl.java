package rocks.metaldetector.service.token;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rocks.metaldetector.service.email.RegistrationVerificationEmail;
import rocks.metaldetector.service.exceptions.ErrorMessages;
import rocks.metaldetector.service.exceptions.ResourceNotFoundException;
import rocks.metaldetector.support.JwtsSupport;
import rocks.metaldetector.persistence.domain.token.TokenEntity;
import rocks.metaldetector.persistence.domain.token.TokenRepository;
import rocks.metaldetector.persistence.domain.token.TokenType;
import rocks.metaldetector.persistence.domain.user.UserEntity;
import rocks.metaldetector.persistence.domain.user.UserRepository;
import rocks.metaldetector.service.email.EmailService;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
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
  public String createEmailVerificationToken(String publicUserId) {
    return createToken(publicUserId, TokenType.EMAIL_VERIFICATION, Duration.ofDays(10));
  }

  @Override
  @Transactional
  public String createResetPasswordToken(String publicUserId) {
    return createToken(publicUserId, TokenType.PASSWORD_RESET, Duration.ofHours(1));
  }

  private String createToken(String publicUserId, TokenType tokenType, Duration expirationTime) {
    String      tokenString = jwtsSupport.generateToken(publicUserId, expirationTime);
    UserEntity  userEntity  = userRepository.findByPublicId(publicUserId).orElseThrow(
            () -> new ResourceNotFoundException(ErrorMessages.USER_WITH_ID_NOT_FOUND.toDisplayString())
    );

    TokenEntity tokenEntity = TokenEntity.builder()
                                         .user(userEntity)
                                         .tokenString(tokenString)
                                         .expirationDateTime(LocalDateTime.now().plus(expirationTime.toMillis(), ChronoUnit.MILLIS))
                                         .tokenType(tokenType)
                                         .build();

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
    String newTokenString = createEmailVerificationToken(userEntity.getPublicId());
    emailService.sendEmail(new RegistrationVerificationEmail(userEntity.getEmail(), newTokenString));
  }

  @Override
  @Transactional
  public void deleteToken(TokenEntity tokenEntity) {
    tokenRepository.delete(tokenEntity);
  }

}
