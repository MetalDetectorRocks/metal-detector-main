package rocks.metaldetector.service.token;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rocks.metaldetector.persistence.domain.user.AbstractUserEntity;
import rocks.metaldetector.persistence.domain.user.UserRepository;
import rocks.metaldetector.service.email.EmailService;
import rocks.metaldetector.service.email.RegistrationVerificationEmail;
import rocks.metaldetector.service.exceptions.TokenExpiredException;
import rocks.metaldetector.support.JwtsSupport;
import rocks.metaldetector.support.exceptions.ResourceNotFoundException;

import java.time.Duration;
import java.util.Date;

import static rocks.metaldetector.service.user.UserErrorMessages.USER_WITH_ID_NOT_FOUND;

@Service
@AllArgsConstructor
public class TokenServiceImpl implements TokenService {

  private final UserRepository userRepository;
  private final EmailService emailService;
  private final JwtsSupport jwtsSupport;

  @Override
  public String createEmailVerificationToken(String publicUserId) {
    return createToken(publicUserId, Duration.ofDays(10));
  }

  @Override
  public String createResetPasswordToken(String publicUserId) {
    return createToken(publicUserId, Duration.ofHours(1));
  }

  private String createToken(String publicUserId, Duration expirationTime) {
    return jwtsSupport.generateToken(publicUserId, expirationTime);
  }

  @Override
  @Transactional
  public void resendExpiredEmailVerificationToken(String tokenString) {
    var claims = jwtsSupport.getClaims(tokenString);
    AbstractUserEntity userEntity = userRepository.findByPublicId(claims.getSubject())
            .orElseThrow(() -> new ResourceNotFoundException(USER_WITH_ID_NOT_FOUND.toDisplayString()));
    String newTokenString = createEmailVerificationToken(userEntity.getPublicId());
    emailService.sendEmail(new RegistrationVerificationEmail(userEntity.getEmail(), userEntity.getUsername(), newTokenString));
  }

  @Override
  @Transactional
  public void verifyEmailToken(String tokenString) {
    // get claims to check signature of token
    var claims = jwtsSupport.getClaims(tokenString);

    // check if token is expired
    if (claims.getExpiration().before(new Date())) {
      throw new TokenExpiredException();
    }

    // verify registration
    AbstractUserEntity userEntity = userRepository.findByPublicId(claims.getSubject())
        .orElseThrow(() -> new ResourceNotFoundException(USER_WITH_ID_NOT_FOUND.toDisplayString()));
    userEntity.setEnabled(true);
    userRepository.save(userEntity);
  }
}
