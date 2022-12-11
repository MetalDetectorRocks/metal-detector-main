package rocks.metaldetector.support;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Stream;

import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class JwtsSupportTest implements WithAssertions {

  private static final String TOKEN_SECRET = "dummyTokenSecretdummyTokenSecretdummyTokenSecretdummyTokenSecretdummyTokenSecretdummyTokenSecretdummyTokenSecret";
  private static final String TOKEN_ISSUER = "dummy-token-issuer";

  @Mock
  private SecurityProperties securityProperties;

  @InjectMocks
  private JwtsSupport underTest;

  @Test
  @DisplayName("generateToken() should generate a new and unique token")
  void generate_token_should_generate_a_new_and_unique_token() {
    // given
    doReturn(TOKEN_SECRET).when(securityProperties).getJwtSecret();

    // when
    String token1 = underTest.generateToken("Dummy Subject", Duration.ofHours(1));
    String token2 = underTest.generateToken("Dummy Subject", Duration.ofHours(1));

    // then
    assertThat(token1).isNotNull();
    assertThat(token1).isNotEqualTo(token2);
  }

  @Test
  @DisplayName("getClaims() should return correct values from token")
  void get_claims_should_return_correct_values_from_token() {
    // given
    doReturn(TOKEN_ISSUER).when(securityProperties).getJwtIssuer();
    doReturn(TOKEN_SECRET).when(securityProperties).getJwtSecret();
    final String SUBJECT = "Dummy Subject";
    long currentMillis = System.currentTimeMillis();

    // when
    String token = underTest.generateToken(SUBJECT, Duration.ofHours(1));
    Claims claims = underTest.getClaims(token);

    // then
    assertThat(claims.getId()).isNotNull();
    assertThat(UUID.fromString(claims.getId())).isNotNull();
    assertThat(claims.getSubject()).isEqualTo(SUBJECT);
    assertThat(claims.getExpiration()).isCloseTo(new Date(currentMillis + Duration.ofHours(1).toMillis()), 1_000L);
    assertThat(claims.getIssuedAt()).isCloseTo(new Date(currentMillis), 1_000L);
    assertThat(claims.getIssuer()).isEqualTo(TOKEN_ISSUER);
  }

  @Test
  @DisplayName("should successfully validate token")
  void should_successfully_validate_token() {
    // given
    doReturn(TOKEN_ISSUER).when(securityProperties).getJwtIssuer();
    doReturn(TOKEN_SECRET).when(securityProperties).getJwtSecret();
    String token = underTest.generateToken("foo", Duration.ofMinutes(1));

    // when
    boolean result = underTest.validateJwtToken(token);

    // then
    assertThat(result).isTrue();
  }

  @Test
  @DisplayName("should validate expiration time")
  void should_validate_expiration_time() {
    // given
    String expiredToken = Jwts.builder()
        .setSubject(UUID.randomUUID().toString())
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() - 1))
        .signWith(SignatureAlgorithm.HS512, "someKeysomeKeysomeKeysomeKeysomeKeysomeKeysomeKeysomeKeysomeKeysomeKeysomeKeysomeKeysomeKey")
        .compact();

    // when
    boolean result = underTest.validateJwtToken(expiredToken);

    // then
    assertThat(result).isFalse();
  }

  @ParameterizedTest(name = "<{0}> should be invalid")
  @MethodSource("invalidTokenProvider")
  @DisplayName("should validate token form")
  void should_validate_token_form(String invalidToken) {
    // when
    boolean result = underTest.validateJwtToken(invalidToken);

    // then
    assertThat(result).isFalse();
  }

  private static Stream<Arguments> invalidTokenProvider() {
    return Stream.of(
        Arguments.of((String) null),
        Arguments.of(""),
        Arguments.of("something")
    );
  }
}
