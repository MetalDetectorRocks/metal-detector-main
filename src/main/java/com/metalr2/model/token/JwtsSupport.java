package com.metalr2.model.token;

import com.metalr2.security.ExpirationTime;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtsSupport {

  @Value("${token-secret}")
  private String TOKEN_SECRET;

  public String generateToken(String subject, ExpirationTime expirationTime) {
    return Jwts.builder()
               .setSubject(subject)
               .setExpiration(new Date(System.currentTimeMillis() + expirationTime.toMillis()))
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
