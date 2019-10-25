package com.metalr2.service.token;

import com.metalr2.model.email.AbstractEmail;
import com.metalr2.model.email.RegistrationVerificationEmail;
import com.metalr2.model.token.*;
import com.metalr2.model.user.UserEntity;
import com.metalr2.model.user.UserFactory;
import com.metalr2.model.user.UserRepository;
import com.metalr2.security.ExpirationTime;
import com.metalr2.service.email.EmailService;
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

import java.util.Optional;

import static org.mockito.Mockito.*;

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
    final TokenEntity tokenEntity = TokenFactory.createToken(TokenType.PASSWORD_RESET, ExpirationTime.ONE_HOUR.toMillis());
    when(tokenRepository.findResetPasswordToken(TOKEN)).thenReturn(Optional.of(tokenEntity));

    Optional<TokenEntity> result = tokenService.getResetPasswordTokenByTokenString(TOKEN);

    verify(tokenRepository, times(1)).findResetPasswordToken(TOKEN);
    assertThat(result).isPresent();
    assertThat(result.get().getTokenString()).isEqualTo(tokenEntity.getTokenString());
    assertThat(result.get().getTokenType()).isEqualTo(TokenType.PASSWORD_RESET);
  }

  @Test
  @DisplayName("getResetPasswordTokenByTokenString() for a not existing token string should return the an empty optional")
  void get_reset_password_token_by_token_string_should_return_empty_optional() {
    when(tokenRepository.findResetPasswordToken(TOKEN)).thenReturn(Optional.empty());

    Optional<TokenEntity> result = tokenService.getResetPasswordTokenByTokenString(TOKEN);

    verify(tokenRepository, times(1)).findResetPasswordToken(TOKEN);
    assertThat(result).isEmpty();
  }

  @Test
  @DisplayName("createEmailVerificationToken() should create a new email verification token")
  void create_email_verification_token_should_create_token_for_email_verification() {
    ArgumentCaptor<TokenEntity> tokenEntityArgumentCaptor = ArgumentCaptor.forClass(TokenEntity.class);
    UserEntity userEntity = UserFactory.createUser("JohnD", "johnd@example.com");
    when(userRepository.findByPublicId(PUBLIC_USER_ID)).thenReturn(Optional.of(userEntity));
    when(jwtsSupport.generateToken(PUBLIC_USER_ID, ExpirationTime.TEN_DAYS)).thenReturn(TOKEN);

    String createdTokenString = tokenService.createEmailVerificationToken(PUBLIC_USER_ID);

    assertThat(createdTokenString).isEqualTo(TOKEN);
    verify(jwtsSupport, times(1)).generateToken(PUBLIC_USER_ID, ExpirationTime.TEN_DAYS);
    verify(userRepository, times(1)).findByPublicId(PUBLIC_USER_ID);
    verify(tokenRepository, times(1)).save(tokenEntityArgumentCaptor.capture());
    assertThat(tokenEntityArgumentCaptor.getValue().getTokenType()).isEqualTo(TokenType.EMAIL_VERIFICATION);
  }

  @Test
  @DisplayName("createResetPasswordToken() should create a new reset password token")
  void create_reset_password_token_should_create_token_for_password_change() {
    ArgumentCaptor<TokenEntity> tokenEntityArgumentCaptor = ArgumentCaptor.forClass(TokenEntity.class);
    UserEntity userEntity = UserFactory.createUser("JohnD", "johnd@example.com");
    when(userRepository.findByPublicId(PUBLIC_USER_ID)).thenReturn(Optional.of(userEntity));
    when(jwtsSupport.generateToken(PUBLIC_USER_ID, ExpirationTime.ONE_HOUR)).thenReturn(TOKEN);

    String createdTokenString = tokenService.createResetPasswordToken(PUBLIC_USER_ID);

    assertThat(createdTokenString).isEqualTo(TOKEN);
    verify(jwtsSupport, times(1)).generateToken(PUBLIC_USER_ID, ExpirationTime.ONE_HOUR);
    verify(userRepository, times(1)).findByPublicId(PUBLIC_USER_ID);
    verify(tokenRepository, times(1)).save(tokenEntityArgumentCaptor.capture());
    assertThat(tokenEntityArgumentCaptor.getValue().getTokenType()).isEqualTo(TokenType.PASSWORD_RESET);
  }

  @Test
  @DisplayName("resendExpiredEmailVerificationToken() should send...")
  void resend_expired_email_verification_token_should_send_new_email() {
    // create token entity for mocking with spied user
    UserEntity userOfToken = UserFactory.createUser("JohnD", "johnd@example.com");
    userOfToken = Mockito.spy(userOfToken);
    TokenEntity tokenEntity = TokenFactory.createToken(TokenType.EMAIL_VERIFICATION, userOfToken);

    // init argument captor
    ArgumentCaptor<AbstractEmail> emailArgumentCaptor = ArgumentCaptor.forClass(AbstractEmail.class);

    // define mocking behaviour
    when(userOfToken.getPublicId()).thenReturn(PUBLIC_USER_ID);
    when(tokenRepository.findEmailVerificationToken(TOKEN)).thenReturn(Optional.of(tokenEntity));
    when(jwtsSupport.generateToken(PUBLIC_USER_ID, ExpirationTime.TEN_DAYS)).thenReturn(TOKEN);
    when(userRepository.findByPublicId(PUBLIC_USER_ID)).thenReturn(Optional.of(userOfToken));

    tokenService.resendExpiredEmailVerificationToken(TOKEN);

    verify(tokenRepository, times(1)).findEmailVerificationToken(TOKEN);
    verify(tokenRepository, times(1)).delete(tokenEntity);
    verify(emailService, times(1)).sendEmail(emailArgumentCaptor.capture());

    assertThat(emailArgumentCaptor.getValue()).isInstanceOf(RegistrationVerificationEmail.class);
    assertThat(emailArgumentCaptor.getValue().getRecipient()).isEqualTo(userOfToken.getEmail());
  }

  @Test
  @DisplayName("deleteToken() should delete the token")
  void delete_token_should_delete_token() {
    TokenEntity tokenEntity = TokenFactory.createToken();

    tokenService.deleteToken(tokenEntity);

    verify(tokenRepository, times(1)).delete(tokenEntity);
  }

}