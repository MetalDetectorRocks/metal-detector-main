package rocks.metaldetector.service.auth;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import rocks.metaldetector.persistence.domain.user.RefreshTokenEntity;
import rocks.metaldetector.persistence.domain.user.RefreshTokenRepository;
import rocks.metaldetector.persistence.domain.user.UserRepository;
import rocks.metaldetector.service.exceptions.UnauthorizedException;
import rocks.metaldetector.support.JwtsSupport;
import rocks.metaldetector.support.SecurityProperties;

import java.time.Duration;

import static org.apache.tomcat.util.http.SameSiteCookies.LAX;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

  static final int OFFSET_IN_MINUTES = 5;
  public static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";

  private final RefreshTokenRepository refreshTokenRepository;
  private final UserRepository userRepository;
  private final SecurityProperties securityProperties;
  private final JwtsSupport jwtsSupport;
  private String domain;

  @Transactional
  public ResponseCookie createRefreshTokenCookie(String username) {
    RefreshTokenEntity refreshTokenEntity = new RefreshTokenEntity();
    refreshTokenEntity = refreshTokenRepository.save(refreshTokenEntity);

    String refreshToken = createRefreshToken(refreshTokenEntity.getId().toString());
    refreshTokenEntity.setToken(refreshToken);
    refreshTokenEntity.setUser(userRepository.getByUsername(username));

    return createCookie(refreshToken);
  }

  @Transactional
  public RefreshTokenData refreshTokens(String refreshToken) {
    if (!refreshTokenRepository.existsByToken(refreshToken) || !jwtsSupport.validateJwtToken(refreshToken)) {
      throw new UnauthorizedException();
    }

    RefreshTokenEntity refreshTokenEntity = refreshTokenRepository.getByToken(refreshToken);
    String accessToken = createAccessToken(refreshTokenEntity.getUser().getPublicId());
    String newRefreshToken = createRefreshToken(refreshTokenEntity.getId().toString());
    refreshTokenEntity.setToken(newRefreshToken);

    return new RefreshTokenData(
        refreshTokenEntity.getUser().getUsername(),
        refreshTokenEntity.getUser().getUserRoleNames(),
        accessToken,
        createCookie(newRefreshToken)
    );
  }

  @Transactional
  public void removeRefreshToken(String tokenValue) {
    refreshTokenRepository.deleteByToken(tokenValue);
  }

  @Transactional
  public boolean isValid(String tokenValue) {
    return tokenValue != null && refreshTokenRepository.existsByToken(tokenValue) && jwtsSupport.validateJwtToken(tokenValue);
  }

  private String createRefreshToken(String tokenEntityId) {
    Duration duration = Duration.ofMinutes(securityProperties.getRefreshTokenExpirationInMin());
    return jwtsSupport.generateToken(tokenEntityId, duration);
  }

  public String createAccessToken(String publicUserId) {
    Duration duration = Duration.ofMinutes(securityProperties.getAccessTokenExpirationInMin());
    return jwtsSupport.generateToken(publicUserId, duration);
  }

  private ResponseCookie createCookie(String refreshToken) {
    return ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, refreshToken)
        .maxAge(Duration.ofMinutes(securityProperties.getRefreshTokenExpirationInMin() - OFFSET_IN_MINUTES))
        .secure(securityProperties.isSecureCookie())
        .httpOnly(true)
        .path("/")
        .domain(domain)
        .sameSite(LAX.getValue())
        .build();
  }

  @Value("${application.domain}")
  void setDomain(String domain) {
    this.domain = domain;
  }
}
