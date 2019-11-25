package com.metalr2.model.token;

import io.jsonwebtoken.Claims;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Date;
import java.util.UUID;

class JwtsSupportTest implements WithAssertions {

  private static final String TOKEN_SECRET = "dummy-token-secret";
  private static final String TOKEN_ISSUER = "dummy-token-issuer";
  private JwtsSupport jwtsSupport;

  @BeforeEach
  void setup() {
    jwtsSupport = new JwtsSupport(TOKEN_SECRET, TOKEN_ISSUER);
  }

  @Test
  @DisplayName("generateToken() should generate a new and unique token")
  void generate_token_should_generate_a_new_and_unique_token() {
    String token1 = jwtsSupport.generateToken("Dummy Subject", Duration.ofHours(1));
    String token2 = jwtsSupport.generateToken("Dummy Subject", Duration.ofHours(1));

    assertThat(token1).isNotNull();
    assertThat(token1).isNotEqualTo(token2);
  }

  @Test
  @DisplayName("getClaims() should return correct values from token")
  void get_claims_should_return_correct_values_from_token() {
    final String SUBJECT = "Dummy Subject";
    long currentMillis = System.currentTimeMillis();
    String token = jwtsSupport.generateToken(SUBJECT, Duration.ofHours(1));

    Claims claims = jwtsSupport.getClaims(token);

    assertThat(claims.getId()).isNotNull();
    assertThat(UUID.fromString(claims.getId())).isNotNull();
    assertThat(claims.getSubject()).isEqualTo(SUBJECT);
    assertThat(claims.getExpiration()).isCloseTo(new Date(currentMillis + Duration.ofHours(1).toMillis()), 1_000L);
    assertThat(claims.getIssuedAt()).isCloseTo(new Date(currentMillis), 1_000L);
    assertThat(claims.getIssuer()).isEqualTo(TOKEN_ISSUER);
  }

}
