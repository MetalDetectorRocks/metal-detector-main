package com.metalr2.model.user.listener;

import com.metalr2.config.constants.ViewNames;
import com.metalr2.model.email.AbstractEmail;
import com.metalr2.model.user.events.OnRegistrationCompleteEvent;
import com.metalr2.service.email.EmailService;
import com.metalr2.service.token.TokenService;
import com.metalr2.web.dto.UserDto;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegistrationCompleteListenerTest implements WithAssertions {

  @Mock
  private EmailService emailService;

  @Mock
  private TokenService tokenService;

  @Captor
  private ArgumentCaptor<AbstractEmail> emailCaptor;

  @InjectMocks
  private RegistrationCompleteListener listener;

  @Test
  @DisplayName("RegistrationCompleteListener should create a new token and send an email after registration")
  void create_token_and_send_email_on_registration() {
    // given
    final String EMAIL = "john.doe@example.com";
    final String TOKEN = "1234-56789";
    UserDto userDto = UserDto.builder().publicId("public-id").email(EMAIL).build();
    OnRegistrationCompleteEvent event = new OnRegistrationCompleteEvent("source", userDto);
    when(tokenService.createEmailVerificationToken(userDto.getPublicId())).thenReturn(TOKEN);

    // when
    listener.onApplicationEvent(event);

    // then
    verify(tokenService, times(1)).createEmailVerificationToken(userDto.getPublicId());
    verify(emailService, times(1)).sendEmail(emailCaptor.capture());

    AbstractEmail email = emailCaptor.getValue();
    String verificationUrl = (String) email.getEnhancedViewModel("dummy-base-url").get("verificationUrl");
    assertThat(email.getRecipient()).isEqualTo(EMAIL);
    assertThat(email.getSubject()).isEqualTo("One last step to complete your registration!");
    assertThat(email.getTemplateName()).isEqualTo(ViewNames.EmailTemplates.REGISTRATION_VERIFICATION);
    assertThat(verificationUrl).endsWith(TOKEN);
  }
}
