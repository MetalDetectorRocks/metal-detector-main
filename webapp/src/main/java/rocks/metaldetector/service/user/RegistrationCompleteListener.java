package rocks.metaldetector.service.user;

import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import rocks.metaldetector.service.email.EmailService;
import rocks.metaldetector.service.email.RegistrationVerificationEmail;
import rocks.metaldetector.service.token.TokenService;

@Component
@AllArgsConstructor
public class RegistrationCompleteListener implements ApplicationListener<OnRegistrationCompleteEvent> {

  private final EmailService emailService;
  private final TokenService tokenService;

  @Override
  public void onApplicationEvent(@NonNull OnRegistrationCompleteEvent event) {
    sendConfirmationEmail(event);
  }

  private void sendConfirmationEmail(OnRegistrationCompleteEvent event) {
    UserDto userDto = event.getUserDto();
    String token = tokenService.createEmailVerificationToken(userDto.getPublicId());
    emailService.sendEmail(new RegistrationVerificationEmail(userDto.getEmail(), userDto.getUsername(), token));
  }
}
