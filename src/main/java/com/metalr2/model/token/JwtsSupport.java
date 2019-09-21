package com.metalr2.model.token;

import com.metalr2.security.ExpirationTime;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

@Component
@PropertySource(value = "classpath:security.properties")
public class JwtsSupport {

  @Value("${security.token-secret}")
  private String TOKEN_SECRET;

  @Value("${security.token-issuer}")
  private String TOKEN_ISSUER;

  public String generateToken(String subject, ExpirationTime expirationTime) {
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
