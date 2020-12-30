package rocks.metaldetector.service.token;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rocks.metaldetector.persistence.domain.token.TokenEntity;
import rocks.metaldetector.persistence.domain.token.TokenRepository;
import rocks.metaldetector.persistence.domain.token.TokenType;
import rocks.metaldetector.persistence.domain.user.UserEntity;
import rocks.metaldetector.persistence.domain.user.UserRepository;
import rocks.metaldetector.service.email.EmailService;
import rocks.metaldetector.service.email.RegistrationVerificationEmail;
import rocks.metaldetector.service.user.UserErrorMessages;
import rocks.metaldetector.support.JwtsSupport;
import rocks.metaldetector.support.exceptions.ResourceNotFoundException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
@AllArgsConstructor
public class TokenServiceImpl implements TokenService {

  private final TokenRepository tokenRepository;
  private final UserRepository userRepository;
  private final EmailService emailService;
  private final JwtsSupport jwtsSupport;

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
            () -> new ResourceNotFoundException(UserErrorMessages.USER_WITH_ID_NOT_FOUND.toDisplayString())
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
                                             .orElseThrow(() -> new ResourceNotFoundException(UserErrorMessages.TOKEN_NOT_FOUND.toDisplayString()));
    UserEntity userEntity = tokenEntity.getUser();

    tokenRepository.delete(tokenEntity);
    String newTokenString = createEmailVerificationToken(userEntity.getPublicId());
    emailService.sendEmail(new RegistrationVerificationEmail(userEntity.getEmail(), userEntity.getUsername(), newTokenString));
  }

  @Override
  @Transactional
  public void deleteToken(TokenEntity tokenEntity) {
    tokenRepository.delete(tokenEntity);
  }

}
