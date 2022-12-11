package rocks.metaldetector.service.user;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseCookie;
import rocks.metaldetector.persistence.domain.user.RefreshTokenEntity;
import rocks.metaldetector.persistence.domain.user.RefreshTokenRepository;
import rocks.metaldetector.persistence.domain.user.UserEntity;
import rocks.metaldetector.persistence.domain.user.UserRepository;
import rocks.metaldetector.support.JwtsSupport;
import rocks.metaldetector.support.SecurityProperties;

import java.time.Duration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static rocks.metaldetector.service.user.RefreshTokenService.COOKIE_NAME;
import static rocks.metaldetector.service.user.RefreshTokenService.OFFSET_IN_MINUTES;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest implements WithAssertions {

  @Mock
  private RefreshTokenRepository refreshTokenRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private SecurityProperties securityProperties;

  @Mock
  private JwtsSupport jwtsSupport;

  @Mock
  private RefreshTokenEntity refreshToken;

  @InjectMocks
  private RefreshTokenService underTest;

  @BeforeEach
  void beforeEach() {
    doReturn(1L).when(refreshToken).getId();
    doReturn(refreshToken).when(refreshTokenRepository).save(any());
  }

  @AfterEach
  void tearDown() {
    reset(refreshTokenRepository, userRepository, securityProperties, jwtsSupport);
  }

  @Test
  @DisplayName("should create new refresh token entity")
  void should_create_new_refresh_token_entity() {
    // when
    underTest.createRefreshTokenCookie("foobar");

    // then
    refreshTokenRepository.save(any());
  }

  @Test
  @DisplayName("should generate refresh token with configured duration")
  void should_generate_refresh_token_with_configured_duration() {
    // given
    doReturn(666L).when(securityProperties).getRefreshTokenExpirationInMin();

    // when
    underTest.createRefreshTokenCookie("foobar");

    // then
    verify(jwtsSupport).generateToken(refreshToken.getId().toString(), Duration.ofMinutes(666));
  }

  @Test
  @DisplayName("should set refresh token into entity")
  void should_set_refresh_token_into_entity() {
    // given
    var sampleToken = "sample-token";
    doReturn(sampleToken).when(jwtsSupport).generateToken(any(), any());

    // when
    underTest.createRefreshTokenCookie("foobar");

    // then
    ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
    verify(refreshToken).setToken(captor.capture());
    assertThat(captor.getValue()).isEqualTo(sampleToken);
  }

  @Test
  @DisplayName("should set user into entity")
  void should_set_user_into_entity() {
    // given
    var sampleUser = UserEntityFactory.createUser("user", "user@example.com");
    doReturn(sampleUser).when(userRepository).getByUsername(any());

    // when
    underTest.createRefreshTokenCookie("foobar");

    // then
    ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
    verify(refreshToken).setUser(captor.capture());
    assertThat(captor.getValue()).isEqualTo(sampleUser);
  }

  @Test
  @DisplayName("should return http cookie with expected attributes")
  void should_return_http_cookie_with_max_age() {
    // given
    var domain = "example.com";
    doReturn(666L).when(securityProperties).getRefreshTokenExpirationInMin();
    doReturn(true).when(securityProperties).isSecureCookie();
    underTest.setDomain(domain);

    // when
    ResponseCookie cookie = underTest.createRefreshTokenCookie("foobar");

    // then
    assertThat(cookie.getName()).isEqualTo(COOKIE_NAME);
    assertThat(cookie.getMaxAge()).isEqualTo(Duration.ofMinutes(666 - OFFSET_IN_MINUTES));
    assertThat(cookie.getSameSite()).isEqualTo("Strict");
    assertThat(cookie.getPath()).isEqualTo("/");
    assertThat(cookie.isHttpOnly()).isTrue();
    assertThat(cookie.isSecure()).isTrue();
    assertThat(cookie.getDomain()).isEqualTo(domain);
  }
}
