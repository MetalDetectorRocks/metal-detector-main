package com.metalr2.model.token;

import com.metalr2.security.ExpirationTime;
import io.jsonwebtoken.Claims;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.UUID;

class JwtsSupportTest implements WithAssertions {

  private static final String TOKEN_SECRET = "dummy-token-secret";
  private static final String TOKEN_ISSUER = "dummy issuer";

  private JwtsSupport jwtsSupport;

  @BeforeEach
  void setUp() {
    jwtsSupport = new JwtsSupport();
    jwtsSupport.setTokenSecret(TOKEN_SECRET);
    jwtsSupport.setTokenIssuer(TOKEN_ISSUER);
  }

  @Test
  void generateTokenShouldGenerateANewToken() {
    String token1 = jwtsSupport.generateToken("Dummy Subject", ExpirationTime.ONE_HOUR);
    String token2 = jwtsSupport.generateToken("Dummy Subject", ExpirationTime.ONE_HOUR);

    assertThat(token1).isNotNull();
    assertThat(token1).isNotEqualTo(token2);
  }

  @Test
  void getClaimsShouldReturnCorrectValues() {
    final String SUBJECT = "Dummy Subject";
    long currentMillis = System.currentTimeMillis();
    String token = jwtsSupport.generateToken(SUBJECT, ExpirationTime.ONE_HOUR);

    Claims claims = jwtsSupport.getClaims(token);

    assertThat(claims.getId()).isNotNull();
    assertThat(UUID.fromString(claims.getId())).isNotNull();
    assertThat(claims.getSubject()).isEqualTo(SUBJECT);
    assertThat(claims.getExpiration()).isCloseTo(new Date(currentMillis + ExpirationTime.ONE_HOUR.toMillis()), 1_000L);
    assertThat(claims.getIssuedAt()).isCloseTo(new Date(currentMillis), 1_000L);
    assertThat(claims.getIssuer()).isEqualTo(TOKEN_ISSUER);
  }

  @Test
  void setTokenSecretTwiceShouldThrowException() {
    Throwable setTokenSecret = catchThrowable(() -> jwtsSupport.setTokenSecret("secret"));

    assertThat(setTokenSecret).isInstanceOf(UnsupportedOperationException.class);
    assertThat(setTokenSecret).hasMessage("The value may only be set once.");
  }

  @Test
  void setTokenIssuerTwiceShouldThrowException() {
    Throwable setTokenIssuer = catchThrowable(() -> jwtsSupport.setTokenIssuer("issuer"));

    assertThat(setTokenIssuer).isInstanceOf(UnsupportedOperationException.class);
    assertThat(setTokenIssuer).hasMessage("The value may only be set once.");
  }

}
