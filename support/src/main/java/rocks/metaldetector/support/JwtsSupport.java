package rocks.metaldetector.support;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Date;
import java.util.UUID;

import static io.jsonwebtoken.SignatureAlgorithm.HS512;

@Component
@PropertySource(value = "classpath:application.yml")
@RequiredArgsConstructor
public class JwtsSupport {

  private final SecurityProperties securityProperties;

  public String generateToken(String subject, Duration expirationTime) {
    long currentTimeMillis = System.currentTimeMillis();
    return Jwts.builder()
               .setSubject(subject)
               .setId(UUID.randomUUID().toString())
               .setIssuedAt(new Date(currentTimeMillis))
               .setIssuer(securityProperties.getTokenIssuer())
               .setExpiration(new Date(currentTimeMillis + expirationTime.toMillis()))
               .signWith(HS512, securityProperties.getTokenSecret())
               .compact();
  }

  public Claims getClaims(String token) {
    return Jwts.parser()
               .setSigningKey(securityProperties.getTokenSecret())
               .parseClaimsJws(token)
               .getBody();
  }
}
