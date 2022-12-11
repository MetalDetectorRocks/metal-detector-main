package rocks.metaldetector.service.user;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import rocks.metaldetector.persistence.domain.user.RefreshTokenEntity;
import rocks.metaldetector.persistence.domain.user.RefreshTokenRepository;
import rocks.metaldetector.persistence.domain.user.UserRepository;
import rocks.metaldetector.support.JwtsSupport;
import rocks.metaldetector.support.SecurityProperties;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

  static final int OFFSET_IN_MINUTES = 5;
  static final String COOKIE_NAME = "refresh_token";

  private final RefreshTokenRepository refreshTokenRepository;
  private final UserRepository userRepository;
  private final SecurityProperties securityProperties;
  private final JwtsSupport jwtsSupport;
  private String domain;

  @Transactional
  public ResponseCookie createRefreshTokenCookie(String username) {
    RefreshTokenEntity refreshTokenEntity = new RefreshTokenEntity();
    refreshTokenEntity = refreshTokenRepository.save(refreshTokenEntity);

    Duration duration = Duration.ofMinutes(securityProperties.getRefreshTokenExpirationInMin());
    String refreshToken = jwtsSupport.generateToken(refreshTokenEntity.getId().toString(), duration);
    refreshTokenEntity.setToken(refreshToken);
    refreshTokenEntity.setUser(userRepository.getByUsername(username));

    return createCookie(refreshToken);
  }

  private ResponseCookie createCookie(String refreshToken) {
    return ResponseCookie.from(COOKIE_NAME, refreshToken)
        .maxAge(Duration.ofMinutes(securityProperties.getRefreshTokenExpirationInMin() - OFFSET_IN_MINUTES))
        .secure(securityProperties.isSecureCookie())
        .httpOnly(true)
        .path("/")
        .domain(domain)
        .sameSite("Strict")
        .build();
  }

  @Value("${application.domain}")
  void setDomain(String domain) {
    this.domain = domain;
  }
}
