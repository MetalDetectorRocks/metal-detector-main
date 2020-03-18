package rocks.metaldetector.model.token;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Date;
import java.util.UUID;

@Component
@PropertySource(value = "classpath:application.properties")
public class JwtsSupport {

  private final String TOKEN_SECRET;
  private final String TOKEN_ISSUER;

  public JwtsSupport(@Value("${security.token-secret}") String tokenSecret,
                     @Value("${security.token-issuer}") String tokenIssuer) {
    TOKEN_SECRET = tokenSecret;
    TOKEN_ISSUER = tokenIssuer;
  }

  public String generateToken(String subject, Duration expirationTime) {
    long currentTimeMillis = System.currentTimeMillis();
    return Jwts.builder()
               .setSubject(subject)
               .setId(UUID.randomUUID().toString())
               .setIssuedAt(new Date(currentTimeMillis))
               .setIssuer(TOKEN_ISSUER)
               .setExpiration(new Date(currentTimeMillis + expirationTime.toMillis()))
               .signWith(SignatureAlgorithm.HS512, TOKEN_SECRET)
               .compact();
  }

  public Claims getClaims(String token) {
    return Jwts.parser()
               .setSigningKey(TOKEN_SECRET)
               .parseClaimsJws(token)
               .getBody();
  }

}
