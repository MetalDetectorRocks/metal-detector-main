package rocks.metaldetector.service.auth;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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
import rocks.metaldetector.service.exceptions.UnauthorizedException;
import rocks.metaldetector.service.user.UserEntityFactory;
import rocks.metaldetector.support.JwtsSupport;
import rocks.metaldetector.support.SecurityProperties;

import java.time.Duration;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static rocks.metaldetector.persistence.domain.user.UserRole.ROLE_USER;
import static rocks.metaldetector.service.auth.RefreshTokenService.REFRESH_TOKEN_COOKIE_NAME;
import static rocks.metaldetector.service.auth.RefreshTokenService.OFFSET_IN_MINUTES;

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

  @AfterEach
  void tearDown() {
    reset(refreshTokenRepository, userRepository, securityProperties, jwtsSupport);
  }

  @Nested
  class CreateRefreshTokenCookieTests {

    @BeforeEach
    void beforeEach() {
      doReturn(1L).when(refreshToken).getId();
      doReturn(refreshToken).when(refreshTokenRepository).save(any());
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
      assertThat(cookie.getName()).isEqualTo(REFRESH_TOKEN_COOKIE_NAME);
      assertThat(cookie.getMaxAge()).isEqualTo(Duration.ofMinutes(666 - OFFSET_IN_MINUTES));
      assertThat(cookie.getSameSite()).isEqualTo("Strict");
      assertThat(cookie.getPath()).isEqualTo("/");
      assertThat(cookie.isHttpOnly()).isTrue();
      assertThat(cookie.isSecure()).isTrue();
      assertThat(cookie.getDomain()).isEqualTo(domain);
    }
  }

  @Nested
  class RefreshTokensTest {

    @Mock
    private UserEntity userMock;

    @BeforeEach
    void beforeEach() {
      lenient().doReturn(1L).when(refreshToken).getId();
      lenient().doReturn(refreshToken).when(refreshTokenRepository).getByToken(any());
      lenient().doReturn(userMock).when(refreshToken).getUser();
    }

    @Test
    @DisplayName("should throw UnauthorizedException if refresh token not exists")
    void should_throw_unauthorized_exception_if_refresh_token_not_exists() {
      // given
      var refreshTokenAsString = "eyFoo";
      doReturn(false).when(refreshTokenRepository).existsByToken(refreshTokenAsString);
      doReturn(true).when(jwtsSupport).validateJwtToken(refreshTokenAsString);

      // when
      var throwable = catchThrowable(() -> underTest.refreshTokens(refreshTokenAsString));

      // then
      assertThat(throwable).isInstanceOf(UnauthorizedException.class);
    }

    @Test
    @DisplayName("should throw UnauthorizedException if refresh token is not valid")
    void should_throw_unauthorized_exception_if_refresh_token_is_not_valid() {
      // given
      var refreshTokenAsString = "eyFoo";
      doReturn(true).when(refreshTokenRepository).existsByToken(refreshTokenAsString);
      doReturn(false).when(jwtsSupport).validateJwtToken(refreshTokenAsString);

      // when
      var throwable = catchThrowable(() -> underTest.refreshTokens(refreshTokenAsString));

      // then
      assertThat(throwable).isInstanceOf(UnauthorizedException.class);
    }

    @Test
    @DisplayName("should generate refresh token with configured duration")
    void should_generate_refresh_token_with_configured_duration() {
      // given
      var refreshTokenAsString = "eyFoo";
      doReturn(true).when(refreshTokenRepository).existsByToken(refreshTokenAsString);
      doReturn(true).when(jwtsSupport).validateJwtToken(refreshTokenAsString);
      doReturn(666L).when(securityProperties).getRefreshTokenExpirationInMin();

      // when
      underTest.refreshTokens(refreshTokenAsString);

      // then
      verify(jwtsSupport).generateToken(refreshToken.getId().toString(), Duration.ofMinutes(666));
    }

    @Test
    @DisplayName("should set new refresh token into entity")
    void should_set_new_refresh_token_into_entity() {
      // given
      var refreshTokenAsString = "eyFoo";
      doReturn(true).when(refreshTokenRepository).existsByToken(refreshTokenAsString);
      doReturn(true).when(jwtsSupport).validateJwtToken(refreshTokenAsString);

      var newRefreshToken = "eyBar";
      doReturn(newRefreshToken).when(jwtsSupport).generateToken(any(), any());

      // when
      underTest.refreshTokens(refreshTokenAsString);

      // then
      ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
      verify(refreshToken).setToken(captor.capture());
      assertThat(captor.getValue()).isEqualTo(newRefreshToken);
    }

    @Test
    @DisplayName("should create new access token")
    void should_create_new_access_token() {
      // given
      var refreshTokenAsString = "eyFoo";
      doReturn(true).when(refreshTokenRepository).existsByToken(refreshTokenAsString);
      doReturn(true).when(jwtsSupport).validateJwtToken(refreshTokenAsString);
      doReturn(15L).when(securityProperties).getAccessTokenExpirationInMin();
      doReturn("public-id").when(userMock).getPublicId();

      // when
      underTest.refreshTokens(refreshTokenAsString);

      // then
      verify(jwtsSupport).generateToken("public-id", Duration.ofMinutes(15));
    }

    @Test
    @DisplayName("should return token data with username and roles")
    void should_return_token_data_with_username_and_roles() {
      // given
      var refreshTokenAsString = "eyFoo";
      doReturn("JohnD").when(userMock).getUsername();
      doReturn(List.of(ROLE_USER.getDisplayName())).when(userMock).getUserRoleNames();
      doReturn(true).when(refreshTokenRepository).existsByToken(refreshTokenAsString);
      doReturn(true).when(jwtsSupport).validateJwtToken(refreshTokenAsString);
      doReturn("eyAccessToken", "eyRefreshToken").when(jwtsSupport).generateToken(any(), any());

      // when
      var tokenData = underTest.refreshTokens(refreshTokenAsString);

      // then
      assertThat(tokenData.username()).isEqualTo("JohnD");
      assertThat(tokenData.roles()).containsExactly("User");
    }

    @Test
    @DisplayName("should return token data with access token")
    void should_return_token_data_with_access_token() {
      // given
      var refreshTokenAsString = "eyFoo";
      doReturn(true).when(refreshTokenRepository).existsByToken(refreshTokenAsString);
      doReturn(true).when(jwtsSupport).validateJwtToken(refreshTokenAsString);
      doReturn("eyAccessToken", "eyRefreshToken").when(jwtsSupport).generateToken(any(), any());

      // when
      var tokenData = underTest.refreshTokens(refreshTokenAsString);

      // then
      assertThat(tokenData.accessToken()).isEqualTo("eyAccessToken");
    }

    @Test
    @DisplayName("should return token data with refresh token")
    void should_return_token_data_with_refresh_token() {
      // given
      var domain = "example.com";
      var refreshTokenAsString = "eyFoo";
      doReturn(true).when(refreshTokenRepository).existsByToken(refreshTokenAsString);
      doReturn(true).when(jwtsSupport).validateJwtToken(refreshTokenAsString);
      doReturn(666L).when(securityProperties).getRefreshTokenExpirationInMin();
      doReturn(true).when(securityProperties).isSecureCookie();
      underTest.setDomain(domain);
      doReturn("eyAccessToken", "eyRefreshToken").when(jwtsSupport).generateToken(any(), any());

      // when
      var tokenData = underTest.refreshTokens("eyFoo");

      // then
      ResponseCookie cookie = tokenData.refreshToken();
      assertThat(cookie.getName()).isEqualTo(REFRESH_TOKEN_COOKIE_NAME);
      assertThat(cookie.getMaxAge()).isEqualTo(Duration.ofMinutes(666 - OFFSET_IN_MINUTES));
      assertThat(cookie.getSameSite()).isEqualTo("Strict");
      assertThat(cookie.getPath()).isEqualTo("/");
      assertThat(cookie.isHttpOnly()).isTrue();
      assertThat(cookie.isSecure()).isTrue();
      assertThat(cookie.getDomain()).isEqualTo(domain);
    }
  }

  @Nested
  class DeleteTokenTests {

    @Test
    @DisplayName("should remove refresh token entity via repository")
    void should_remove_refresh_token_entity_via_repository() {
      // given
      String tokenValue = "eyRefreshToken";

      // when
      underTest.removeRefreshToken(tokenValue);

      // then
      verify(refreshTokenRepository).deleteByToken(tokenValue);
    }
  }

  @Nested
  class IsValidTests {

    @Test
    @DisplayName("should be not valid if it is null")
    void should_be_not_valid_if_it_is_null() {
      // when
      boolean result = underTest.isValid(null);

      // then
      assertThat(result).isFalse();
    }

    @Test
    @DisplayName("should be not valid if it does not exist in db")
    void should_be_not_valid_if_it_does_not_exist_in_db() {
      // given
      doReturn(false).when(refreshTokenRepository).existsByToken(any());

      // when
      boolean result = underTest.isValid("eyFoobar");

      // then
      assertThat(result).isFalse();
    }

    @Test
    @DisplayName("should be not valid if it is an invalid jwt")
    void should_be_not_valid_if_it_is_an_invalid_jwt() {
      // given
      doReturn(true).when(refreshTokenRepository).existsByToken(any());
      doReturn(false).when(jwtsSupport).validateJwtToken(any());

      // when
      boolean result = underTest.isValid("eyFoobar");

      // then
      assertThat(result).isFalse();
    }

    @Test
    @DisplayName("should be valid")
    void should_be_valid() {
      // given
      doReturn(true).when(refreshTokenRepository).existsByToken(any());
      doReturn(true).when(jwtsSupport).validateJwtToken(any());

      // when
      boolean result = underTest.isValid("eyFoobar");

      // then
      assertThat(result).isTrue();
    }
  }
}
