package rocks.metaldetector.service.user.events;

import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import rocks.metaldetector.service.email.EmailService;
import rocks.metaldetector.service.email.ForgotPasswordEmail;
import rocks.metaldetector.service.token.TokenService;

@Component
@AllArgsConstructor
public class RequestPasswordResetListener implements ApplicationListener<OnRequestPasswordResetEvent> {

  private final EmailService emailService;
  private final TokenService tokenService;

  @Override
  public void onApplicationEvent(@NonNull OnRequestPasswordResetEvent event) {
    sendResetPasswordEmail(event);
  }

  private void sendResetPasswordEmail(OnRequestPasswordResetEvent event) {
    String token = tokenService.createResetPasswordToken(event.getUserDto().getPublicId());
    emailService.sendEmail(new ForgotPasswordEmail(event.getUserDto().getEmail(), event.getUserDto().getUsername(), token));
  }
}
