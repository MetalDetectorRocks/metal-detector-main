package rocks.metaldetector.service.user;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rocks.metaldetector.config.constants.ViewNames;
import rocks.metaldetector.service.email.AbstractEmail;
import rocks.metaldetector.service.email.EmailService;
import rocks.metaldetector.service.token.TokenService;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResetPasswordRequestCompleteListenerTest implements WithAssertions {

  @Mock
  private EmailService emailService;

  @Mock
  private TokenService tokenService;

  @Captor
  private ArgumentCaptor<AbstractEmail> emailCaptor;

  @InjectMocks
  private ResetPasswordRequestCompleteListener listener;

  @Test
  @DisplayName("ResetPasswordRequestCompleteListener should create token and send an email after password reset request")
  void create_token_and_send_email_on_password_reset() {
    // given
    final String EMAIL = "john.doe@example.com";
    final String TOKEN = "1234-56789";
    UserDto userDto = UserDto.builder().publicId("public-id").email(EMAIL).build();
    OnResetPasswordRequestCompleteEvent event = new OnResetPasswordRequestCompleteEvent("source", userDto);
    when(tokenService.createResetPasswordToken(userDto.getPublicId())).thenReturn(TOKEN);

    // when
    listener.onApplicationEvent(event);

    // then
    verify(tokenService, times(1)).createResetPasswordToken(userDto.getPublicId());
    verify(emailService, times(1)).sendEmail(emailCaptor.capture());

    AbstractEmail email = emailCaptor.getValue();
    String resetPasswordUrl = (String) email.getEnhancedViewModel("dummy-base-url").get("resetPasswordUrl");
    assertThat(email.getRecipient()).isEqualTo(EMAIL);
    assertThat(email.getSubject()).isEqualTo("Your password reset request");
    assertThat(email.getTemplateName()).isEqualTo(ViewNames.EmailTemplates.FORGOT_PASSWORD);
    assertThat(resetPasswordUrl).endsWith(TOKEN);
  }
}
