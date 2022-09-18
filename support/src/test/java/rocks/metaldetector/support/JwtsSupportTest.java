package rocks.metaldetector.support;

import io.jsonwebtoken.Claims;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpCookie;

import java.time.Duration;
import java.util.Date;
import java.util.UUID;

import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class JwtsSupportTest implements WithAssertions {

  private static final String TOKEN_SECRET = "dummy-token-secret";
  private static final String TOKEN_ISSUER = "dummy-token-issuer";

  @Mock
  private SecurityProperties securityProperties;

  @InjectMocks
  private JwtsSupport underTest;

  @Test
  @DisplayName("generateToken() should generate a new and unique token")
  void generate_token_should_generate_a_new_and_unique_token() {
    doReturn(TOKEN_SECRET).when(securityProperties).getTokenSecret();

    String token1 = underTest.generateToken("Dummy Subject", Duration.ofHours(1));
    String token2 = underTest.generateToken("Dummy Subject", Duration.ofHours(1));

    assertThat(token1).isNotNull();
    assertThat(token1).isNotEqualTo(token2);
  }

  @Test
  @DisplayName("getClaims() should return correct values from token")
  void get_claims_should_return_correct_values_from_token() {
    doReturn(TOKEN_ISSUER).when(securityProperties).getTokenIssuer();
    doReturn(TOKEN_SECRET).when(securityProperties).getTokenSecret();
    final String SUBJECT = "Dummy Subject";
    long currentMillis = System.currentTimeMillis();

    String token = underTest.generateToken(SUBJECT, Duration.ofHours(1));
    Claims claims = underTest.getClaims(token);

    assertThat(claims.getId()).isNotNull();
    assertThat(UUID.fromString(claims.getId())).isNotNull();
    assertThat(claims.getSubject()).isEqualTo(SUBJECT);
    assertThat(claims.getExpiration()).isCloseTo(new Date(currentMillis + Duration.ofHours(1).toMillis()), 1_000L);
    assertThat(claims.getIssuedAt()).isCloseTo(new Date(currentMillis), 1_000L);
    assertThat(claims.getIssuer()).isEqualTo(TOKEN_ISSUER);
  }

  @Test
  @DisplayName("should create access token cookie")
  void should_create_access_token_cookie() {
    // given
    String token = "test-token";

    // when
    HttpCookie cookie = underTest.createAccessTokenCookie(token);

    // then
    assertThat(cookie.getName()).isEqualTo("Authorization");
    assertThat(cookie.getValue()).isEqualTo(token);
  }
}
