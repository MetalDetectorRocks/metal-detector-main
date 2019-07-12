package com.metalr2.utils;

import com.metalr2.config.misc.AppProperties;
import com.metalr2.config.misc.SpringApplicationContext;
import com.metalr2.security.ExpirationTime;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

public class JwtsUtils {

  private static String TOKEN_SECRET;

  private JwtsUtils() {
    throw new UnsupportedOperationException("util class - no instance needed!");
  }

  public static String generateToken(String subject, ExpirationTime expirationTime) {
    return Jwts.builder()
               .setSubject(subject)
               .setExpiration(new Date(System.currentTimeMillis() + expirationTime.toMillis()))
               .signWith(SignatureAlgorithm.HS512, getTokenSecret())
               .compact();
  }

  public static Claims getClaims(String token) {
    return Jwts.parser()
               .setSigningKey(getTokenSecret())
               .parseClaimsJws(token)
               .getBody();
  }

  private static String getTokenSecret() {
    if (TOKEN_SECRET == null) {
      AppProperties appProperties = (AppProperties) SpringApplicationContext.getBean("appProperties");
      TOKEN_SECRET = appProperties.getTokenSecret();
    }

    return TOKEN_SECRET;
  }

}
