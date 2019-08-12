package com.metalr2.model.user.listener;

import com.metalr2.model.email.ForgotPasswordEmail;
import com.metalr2.model.user.events.OnResetPasswordRequestCompleteEvent;
import com.metalr2.service.email.EmailService;
import com.metalr2.service.token.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class ResetPasswordRequestCompleteListener implements ApplicationListener<OnResetPasswordRequestCompleteEvent> {

  private EmailService emailService;
  private final TokenService tokenService;

  @Autowired
  public ResetPasswordRequestCompleteListener(EmailService emailService, TokenService tokenService) {
    this.tokenService = tokenService;
    this.emailService = emailService;
  }

  @Override
  public void onApplicationEvent(OnResetPasswordRequestCompleteEvent event) {
    sendResetPasswordEmail(event);
  }

  private void sendResetPasswordEmail(OnResetPasswordRequestCompleteEvent event) {
    String token = tokenService.createResetPasswordToken(event.getUserDto().getPublicId());
    emailService.sendEmail(new ForgotPasswordEmail(event.getUserDto().getEmail(), event.getUserDto().getUsername(), token));
  }
}
