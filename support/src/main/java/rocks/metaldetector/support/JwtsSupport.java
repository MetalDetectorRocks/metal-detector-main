package rocks.metaldetector.support;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Date;
import java.util.UUID;

import static io.jsonwebtoken.SignatureAlgorithm.HS512;

@Component
@PropertySource(value = "classpath:application.yml")
@RequiredArgsConstructor
@Slf4j
public class JwtsSupport {

  private final SecurityProperties securityProperties;

  public String generateToken(String subject, Duration expirationTime) {
    long currentTimeMillis = System.currentTimeMillis();
    return Jwts.builder()
        .setSubject(subject)
        .setId(UUID.randomUUID().toString())
        .setIssuedAt(new Date(currentTimeMillis))
        .setIssuer(securityProperties.getJwtIssuer())
        .setExpiration(new Date(currentTimeMillis + expirationTime.toMillis()))
        .signWith(HS512, securityProperties.getJwtSecret())
        .compact();
  }

  public Claims getClaims(String token) {
    return Jwts.parser()
        .setSigningKey(securityProperties.getJwtSecret())
        .parseClaimsJws(token)
        .getBody();
  }

  public boolean validateJwtToken(String authToken) {
    try {
      Jwts.parser()
          .setSigningKey(securityProperties.getJwtSecret())
          .parseClaimsJws(authToken);
      return true;
    }
    catch (MalformedJwtException e) {
      log.error("Invalid JWT token: {}", e.getMessage());
    }
    catch (ExpiredJwtException e) {
      log.error("JWT token is expired: {}", e.getMessage());
    }
    catch (UnsupportedJwtException e) {
      log.error("JWT token is unsupported: {}", e.getMessage());
    }
    catch (IllegalArgumentException e) {
      log.error("JWT claims string is empty: {}", e.getMessage());
    }

    return false;
  }
}
