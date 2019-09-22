package com.metalr2.model.user.listener;

import com.metalr2.config.constants.ViewNames;
import com.metalr2.model.email.AbstractEmail;
import com.metalr2.model.user.events.OnResetPasswordRequestCompleteEvent;
import com.metalr2.service.email.EmailService;
import com.metalr2.service.token.TokenService;
import com.metalr2.web.dto.UserDto;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

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
