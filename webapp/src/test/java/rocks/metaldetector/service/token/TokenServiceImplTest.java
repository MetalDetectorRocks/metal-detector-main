package rocks.metaldetector.service.token;

import io.jsonwebtoken.Claims;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rocks.metaldetector.persistence.domain.user.UserEntity;
import rocks.metaldetector.persistence.domain.user.UserRepository;
import rocks.metaldetector.service.email.AbstractEmail;
import rocks.metaldetector.service.email.EmailService;
import rocks.metaldetector.service.email.RegistrationVerificationEmail;
import rocks.metaldetector.service.exceptions.TokenExpiredException;
import rocks.metaldetector.service.user.UserEntityFactory;
import rocks.metaldetector.support.JwtsSupport;
import rocks.metaldetector.support.exceptions.ResourceNotFoundException;

import java.time.Duration;
import java.util.Date;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static rocks.metaldetector.service.user.UserErrorMessages.TOKEN_EXPIRED;
import static rocks.metaldetector.service.user.UserErrorMessages.USER_WITH_ID_NOT_FOUND;

@ExtendWith(MockitoExtension.class)
class TokenServiceImplTest implements WithAssertions {

  private final String TOKEN = "token";
  private final String PUBLIC_USER_ID = "public user id";

  @Mock
  private UserRepository userRepository;

  @Mock
  private EmailService emailService;

  @Mock
  private JwtsSupport jwtsSupport;

  @InjectMocks
  private TokenServiceImpl underTest;

  @AfterEach
  void tearDown() {
    reset(userRepository, emailService, jwtsSupport);
  }

  @DisplayName("Tests for creating tokens")
  @Nested
  class CreateTokenTest {

    @Test
    @DisplayName("createEmailVerificationToken() should create a new email verification token")
    void create_email_verification_token_should_create_token_for_email_verification() {
      // given
      doReturn(TOKEN).when(jwtsSupport).generateToken(PUBLIC_USER_ID, Duration.ofDays(10));

      // when
      String createdTokenString = underTest.createEmailVerificationToken(PUBLIC_USER_ID);

      // then
      assertThat(createdTokenString).isEqualTo(TOKEN);
    }

    @Test
    @DisplayName("createEmailVerificationToken() should call jwtsSupport")
    void create_email_verification_token_should_call_jwts_support() {
      // when
      underTest.createEmailVerificationToken(PUBLIC_USER_ID);

      // then
      verify(jwtsSupport).generateToken(PUBLIC_USER_ID, Duration.ofDays(10));
    }

    @Test
    @DisplayName("createResetPasswordToken() should create a new reset password token")
    void create_reset_password_token_should_create_token_for_password_change() {
      // given
      doReturn(TOKEN).when(jwtsSupport).generateToken(PUBLIC_USER_ID, Duration.ofHours(1));

      // when
      String createdTokenString = underTest.createResetPasswordToken(PUBLIC_USER_ID);

      // then
      assertThat(createdTokenString).isEqualTo(TOKEN);
    }

    @Test
    @DisplayName("createResetPasswordToken() should call jwtsSupport")
    void create_reset_password_token_should_call_jwts_support() {
      // when
      underTest.createResetPasswordToken(PUBLIC_USER_ID);

      // then
      verify(jwtsSupport).generateToken(PUBLIC_USER_ID, Duration.ofHours(1));
    }
  }

  @DisplayName("Tests for resending token")
  @Nested
  class ResendTokenTest {

    @Test
    @DisplayName("resendExpiredEmailVerificationToken() should send new mail")
    void resend_expired_email_verification_token_should_send_new_email() {
      // given
      UserEntity userOfToken = UserEntityFactory.createUser("JohnD", "johnd@example.com");
      ArgumentCaptor<AbstractEmail> emailArgumentCaptor = ArgumentCaptor.forClass(AbstractEmail.class);
      doReturn(mock(Claims.class)).when(jwtsSupport).getClaims(any());
      doReturn(Optional.of(userOfToken)).when(userRepository).findByPublicId(any());

      // when
      underTest.resendExpiredEmailVerificationToken(TOKEN);

      // then
      verify(emailService).sendEmail(emailArgumentCaptor.capture());

      assertThat(emailArgumentCaptor.getValue()).isInstanceOf(RegistrationVerificationEmail.class);
      assertThat(emailArgumentCaptor.getValue().getRecipient()).isEqualTo(userOfToken.getEmail());
    }

    @Test
    @DisplayName("resendExpiredEmailVerificationToken() should call jwtsSupport to get claims")
    void resend_expired_email_verification_token_should_call_jwts_support() {
      // given
      UserEntity userOfToken = UserEntityFactory.createUser("JohnD", "johnd@example.com");
      doReturn(mock(Claims.class)).when(jwtsSupport).getClaims(any());
      doReturn(Optional.of(userOfToken)).when(userRepository).findByPublicId(any());

      // when
      underTest.resendExpiredEmailVerificationToken(TOKEN);

      // then
      verify(jwtsSupport).getClaims(TOKEN);
    }

    @Test
    @DisplayName("resendExpiredEmailVerificationToken() should call userRepository")
    void resend_expired_email_verification_token_should_call_user_repository() {
      // given
      UserEntity userOfToken = UserEntityFactory.createUser("JohnD", "johnd@example.com");
      var claims = mock(Claims.class);
      doReturn(PUBLIC_USER_ID).when(claims).getSubject();
      doReturn(claims).when(jwtsSupport).getClaims(any());
      doReturn(Optional.of(userOfToken)).when(userRepository).findByPublicId(any());

      // when
      underTest.resendExpiredEmailVerificationToken(TOKEN);

      // then
      verify(userRepository).findByPublicId(PUBLIC_USER_ID);
    }

    @Test
    @DisplayName("resendExpiredEmailVerificationToken() should throw exception when user is not found")
    void resend_expired_email_verification_token_should_throw_exception() {
      // given
      doReturn(mock(Claims.class)).when(jwtsSupport).getClaims(any());

      // when
      Throwable throwable = catchThrowable(() -> underTest.resendExpiredEmailVerificationToken(TOKEN));

      // then
      assertThat(throwable).isInstanceOf(ResourceNotFoundException.class);
      assertThat(throwable).hasMessageContaining(USER_WITH_ID_NOT_FOUND.toDisplayString());
    }
  }

  @DisplayName("Tests for verifying tokens")
  @Nested
  class VerifyTokenTest {

    @Test
    @DisplayName("Verifying the registration with an existing and not expired token should call jwts support")
    void verify_registration_with_valid_token_should_call_jwts_support() {
      // given
      var token = "token";
      var claims = mock(Claims.class);
      var date = new Date(System.currentTimeMillis() + Duration.ofMinutes(1).toMillis());
      var user = UserEntityFactory.createUser("JohnD", "johnd@example.com");
      doReturn(date).when(claims).getExpiration();
      doReturn(claims).when(jwtsSupport).getClaims(any());
      doReturn(Optional.of(user)).when(userRepository).findByPublicId(any());

      // when
      underTest.verifyEmailToken(token);

      // then
      verify(jwtsSupport).getClaims(token);
    }

    @Test
    @DisplayName("Verifying the registration with an existing and not expired token should call user repository to find user")
    void verify_registration_with_valid_token_should_call_user_repo() {
      // given
      var claims = mock(Claims.class);
      var date = new Date(System.currentTimeMillis() + Duration.ofMinutes(1).toMillis());
      var user = UserEntityFactory.createUser("JohnD", "johnd@example.com");
      doReturn(date).when(claims).getExpiration();
      doReturn(PUBLIC_USER_ID).when(claims).getSubject();
      doReturn(claims).when(jwtsSupport).getClaims(any());
      doReturn(Optional.of(user)).when(userRepository).findByPublicId(any());

      // when
      underTest.verifyEmailToken("token");

      // then
      verify(userRepository).findByPublicId(PUBLIC_USER_ID);
    }

    @Test
    @DisplayName("Verifying the registration with an existing and not expired token should throw exception if user is not found")
    void verify_registration_with_valid_token_should_throw_exception() {
      // given
      var claims = mock(Claims.class);
      var date = new Date(System.currentTimeMillis() + Duration.ofMinutes(1).toMillis());
      doReturn(date).when(claims).getExpiration();
      doReturn(claims).when(jwtsSupport).getClaims(any());

      // when
      Throwable throwable = catchThrowable(() -> underTest.verifyEmailToken("token"));

      // then
      assertThat(throwable).isInstanceOf(ResourceNotFoundException.class);
      assertThat(throwable).hasMessageContaining(USER_WITH_ID_NOT_FOUND.toDisplayString());
    }

    @Test
    @DisplayName("Verifying the registration with an existing and not expired token should save enabled user")
    void verify_registration_with_valid_token() {
      // given
      ArgumentCaptor<UserEntity> userEntityCaptor = ArgumentCaptor.forClass(UserEntity.class);
      var claims = mock(Claims.class);
      var date = new Date(System.currentTimeMillis() + Duration.ofMinutes(1).toMillis());
      var user = UserEntityFactory.createUser("JohnD", "johnd@example.com");
      doReturn(date).when(claims).getExpiration();
      doReturn(PUBLIC_USER_ID).when(claims).getSubject();
      doReturn(claims).when(jwtsSupport).getClaims(any());
      doReturn(Optional.of(user)).when(userRepository).findByPublicId(any());

      // when
      underTest.verifyEmailToken("token");

      // then
      verify(userRepository).save(userEntityCaptor.capture());
      assertThat(userEntityCaptor.getValue().isEnabled()).isTrue();
    }

    @Test
    @DisplayName("Verifying the registration with an expired token should throw exception")
    void verify_registration_with_expired_token() {
      // given
      var claims = mock(Claims.class);
      var date = new Date(System.currentTimeMillis() - Duration.ofMinutes(1).toMillis());
      doReturn(date).when(claims).getExpiration();
      doReturn(claims).when(jwtsSupport).getClaims(any());

      // when
      Throwable throwable = catchThrowable(() -> underTest.verifyEmailToken("token"));

      // then
      assertThat(throwable).isInstanceOf(TokenExpiredException.class);
      assertThat(throwable).hasMessageContaining(TOKEN_EXPIRED.toDisplayString());
    }
  }
}
