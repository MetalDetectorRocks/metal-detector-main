package rocks.metaldetector.service.user.events;

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
import rocks.metaldetector.service.user.UserDto;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
    final String USERNAME = "JohnD";
    UserDto userDto = UserDto.builder().publicId("public-id").email(EMAIL).username(USERNAME).build();
    OnRegistrationCompleteEvent event = new OnRegistrationCompleteEvent("source", userDto);
    when(tokenService.createEmailVerificationToken(userDto.getPublicId())).thenReturn(TOKEN);

    // when
    listener.onApplicationEvent(event);

    // then
    verify(tokenService).createEmailVerificationToken(userDto.getPublicId());
    verify(emailService).sendEmail(emailCaptor.capture());

    AbstractEmail email = emailCaptor.getValue();
    String verificationUrl = (String) email.getEnhancedViewModel("dummy-base-url").get("verificationUrl");
    String username = (String) email.getEnhancedViewModel("dummy-base-url").get("username");
    assertThat(email.getRecipient()).isEqualTo(EMAIL);
    assertThat(email.getSubject()).isEqualTo("One last step to complete your registration!");
    assertThat(email.getTemplateName()).isEqualTo(ViewNames.EmailTemplates.REGISTRATION_VERIFICATION);
    assertThat(verificationUrl).endsWith(TOKEN);
    assertThat(username).isEqualTo(USERNAME);
  }
}
