package com.metalr2.model.token;

import com.metalr2.security.ExpirationTime;
import io.jsonwebtoken.Claims;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.Date;
import java.util.UUID;

@TestPropertySource("classpath:test.properties")
@SpringJUnitConfig
class JwtsSupportTest implements WithAssertions {

  private static final String TOKEN_ISSUER = "dummy-token-issuer";

  @Autowired
  private JwtsSupport jwtsSupport;

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

  @TestConfiguration
  @ComponentScan("com.metalr2.model.token")
  static class TestConfig {
    // needed for autowiring JwtsSupport
  }

}
