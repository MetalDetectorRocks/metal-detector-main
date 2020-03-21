package rocks.metaldetector.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import rocks.metaldetector.service.email.RegistrationVerificationEmail;
import rocks.metaldetector.service.email.EmailService;
import rocks.metaldetector.service.token.TokenService;
import rocks.metaldetector.web.dto.UserDto;

@Component
public class RegistrationCompleteListener implements ApplicationListener<OnRegistrationCompleteEvent> {

  private EmailService emailService;
  private TokenService tokenService;

  @Autowired
  public RegistrationCompleteListener(EmailService emailService, TokenService tokenService) {
    this.tokenService = tokenService;
    this.emailService = emailService;
  }

  @Override
  public void onApplicationEvent(OnRegistrationCompleteEvent event) {
    sendConfirmationEmail(event);
  }

  private void sendConfirmationEmail(OnRegistrationCompleteEvent event) {
    UserDto userDto = event.getUserDto();
    String token    = tokenService.createEmailVerificationToken(userDto.getPublicId());

    emailService.sendEmail(new RegistrationVerificationEmail(userDto.getEmail(), token));
  }
}
