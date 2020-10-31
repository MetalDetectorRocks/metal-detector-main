package rocks.metaldetector.service.token;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import rocks.metaldetector.persistence.domain.token.TokenEntity;
import rocks.metaldetector.persistence.domain.token.TokenRepository;
import rocks.metaldetector.persistence.domain.token.TokenType;
import rocks.metaldetector.persistence.domain.user.UserEntity;
import rocks.metaldetector.persistence.domain.user.UserRepository;
import rocks.metaldetector.service.email.AbstractEmail;
import rocks.metaldetector.service.email.EmailService;
import rocks.metaldetector.service.email.RegistrationVerificationEmail;
import rocks.metaldetector.service.user.UserEntityFactory;
import rocks.metaldetector.service.user.UserErrorMessages;
import rocks.metaldetector.support.JwtsSupport;
import rocks.metaldetector.support.exceptions.ResourceNotFoundException;

import java.time.Duration;
import java.util.Optional;

import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest implements WithAssertions {

  private final String TOKEN          = "token";
  private final String PUBLIC_USER_ID = "public user id";

  @Mock
  private TokenRepository tokenRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private EmailService emailService;

  @Mock
  private JwtsSupport jwtsSupport;

  @InjectMocks
  private TokenServiceImpl tokenService;

  @BeforeEach
  void setUp() {
  }

  @AfterEach
  void tearDown() {
    reset(tokenRepository, userRepository, emailService, jwtsSupport);
  }

  @Test
  @DisplayName("getResetPasswordTokenByTokenString() for a valid token string should return the correct TokenEntity instance")
  void get_reset_password_token_by_token_string_should_return_correct_token_entity_instance() {
    final TokenEntity tokenEntity = TokenFactory.createToken(TokenType.PASSWORD_RESET, Duration.ofHours(1).toMillis());
    when(tokenRepository.findResetPasswordToken(TOKEN)).thenReturn(Optional.of(tokenEntity));

    Optional<TokenEntity> result = tokenService.getResetPasswordTokenByTokenString(TOKEN);

    verify(tokenRepository).findResetPasswordToken(TOKEN);
    assertThat(result).isPresent();
    assertThat(result.get().getTokenString()).isEqualTo(tokenEntity.getTokenString());
    assertThat(result.get().getTokenType()).isEqualTo(TokenType.PASSWORD_RESET);
  }

  @Test
  @DisplayName("getResetPasswordTokenByTokenString() for a not existing token string should return the an empty optional")
  void get_reset_password_token_by_token_string_should_return_empty_optional() {
    when(tokenRepository.findResetPasswordToken(TOKEN)).thenReturn(Optional.empty());

    Optional<TokenEntity> result = tokenService.getResetPasswordTokenByTokenString(TOKEN);

    verify(tokenRepository).findResetPasswordToken(TOKEN);
    assertThat(result).isEmpty();
  }

  @Test
  @DisplayName("createEmailVerificationToken() should create a new email verification token")
  void create_email_verification_token_should_create_token_for_email_verification() {
    ArgumentCaptor<TokenEntity> tokenEntityArgumentCaptor = ArgumentCaptor.forClass(TokenEntity.class);
    UserEntity userEntity = UserEntityFactory.createUser("JohnD", "johnd@example.com");
    when(userRepository.findByPublicId(PUBLIC_USER_ID)).thenReturn(Optional.of(userEntity));
    when(jwtsSupport.generateToken(PUBLIC_USER_ID, Duration.ofDays(10))).thenReturn(TOKEN);

    String createdTokenString = tokenService.createEmailVerificationToken(PUBLIC_USER_ID);

    assertThat(createdTokenString).isEqualTo(TOKEN);
    verify(jwtsSupport).generateToken(PUBLIC_USER_ID, Duration.ofDays(10));
    verify(userRepository).findByPublicId(PUBLIC_USER_ID);
    verify(tokenRepository).save(tokenEntityArgumentCaptor.capture());
    assertThat(tokenEntityArgumentCaptor.getValue().getTokenType()).isEqualTo(TokenType.EMAIL_VERIFICATION);
  }

  @Test
  @DisplayName("createResetPasswordToken() should create a new reset password token")
  void create_reset_password_token_should_create_token_for_password_change() {
    ArgumentCaptor<TokenEntity> tokenEntityArgumentCaptor = ArgumentCaptor.forClass(TokenEntity.class);
    UserEntity userEntity = UserEntityFactory.createUser("JohnD", "johnd@example.com");
    when(userRepository.findByPublicId(PUBLIC_USER_ID)).thenReturn(Optional.of(userEntity));
    when(jwtsSupport.generateToken(PUBLIC_USER_ID, Duration.ofHours(1))).thenReturn(TOKEN);

    String createdTokenString = tokenService.createResetPasswordToken(PUBLIC_USER_ID);

    assertThat(createdTokenString).isEqualTo(TOKEN);
    verify(jwtsSupport).generateToken(PUBLIC_USER_ID, Duration.ofHours(1));
    verify(userRepository).findByPublicId(PUBLIC_USER_ID);
    verify(tokenRepository).save(tokenEntityArgumentCaptor.capture());
    assertThat(tokenEntityArgumentCaptor.getValue().getTokenType()).isEqualTo(TokenType.PASSWORD_RESET);
  }

  @Test
  @DisplayName("Creating a token should fail if user is not found")
  void create_token_should_fail_if_user_not_found() {
    // given
    when(userRepository.findByPublicId(anyString())).thenReturn(Optional.empty());

    // when
    Throwable throwable = catchThrowable(() -> tokenService.createResetPasswordToken(PUBLIC_USER_ID));

    // then
    assertThat(throwable).isInstanceOf(ResourceNotFoundException.class);
    assertThat(throwable).hasMessageContaining(UserErrorMessages.USER_WITH_ID_NOT_FOUND.toDisplayString());
    verify(userRepository).findByPublicId(anyString());
  }

  @Test
  @DisplayName("resendExpiredEmailVerificationToken() should send...")
  void resend_expired_email_verification_token_should_send_new_email() {
    // create token entity for mocking with spied user
    UserEntity userOfToken = UserEntityFactory.createUser("JohnD", "johnd@example.com");
    userOfToken = Mockito.spy(userOfToken);
    TokenEntity tokenEntity = TokenFactory.createToken(TokenType.EMAIL_VERIFICATION, userOfToken);

    // init argument captor
    ArgumentCaptor<AbstractEmail> emailArgumentCaptor = ArgumentCaptor.forClass(AbstractEmail.class);

    // define mocking behaviour
    when(userOfToken.getPublicId()).thenReturn(PUBLIC_USER_ID);
    when(tokenRepository.findEmailVerificationToken(TOKEN)).thenReturn(Optional.of(tokenEntity));
    when(jwtsSupport.generateToken(PUBLIC_USER_ID, Duration.ofDays(10))).thenReturn(TOKEN);
    when(userRepository.findByPublicId(PUBLIC_USER_ID)).thenReturn(Optional.of(userOfToken));

    tokenService.resendExpiredEmailVerificationToken(TOKEN);

    verify(tokenRepository).findEmailVerificationToken(TOKEN);
    verify(tokenRepository).delete(tokenEntity);
    verify(emailService).sendEmail(emailArgumentCaptor.capture());

    assertThat(emailArgumentCaptor.getValue()).isInstanceOf(RegistrationVerificationEmail.class);
    assertThat(emailArgumentCaptor.getValue().getRecipient()).isEqualTo(userOfToken.getEmail());
  }

  @Test
  @DisplayName("deleteToken() should delete the token")
  void delete_token_should_delete_token() {
    TokenEntity tokenEntity = TokenFactory.createDefault();

    tokenService.deleteToken(tokenEntity);

    verify(tokenRepository).delete(tokenEntity);
  }
}
