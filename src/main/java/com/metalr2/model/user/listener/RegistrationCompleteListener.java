package com.metalr2.model.user.listener;

import com.metalr2.model.email.RegistrationVerificationEmail;
import com.metalr2.model.user.events.OnRegistrationCompleteEvent;
import com.metalr2.service.email.EmailService;
import com.metalr2.service.token.TokenService;
import com.metalr2.web.dto.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

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
    String token    = tokenService.createEmailVerificationToken(userDto.getUserId());

    emailService.sendEmail(new RegistrationVerificationEmail(userDto.getEmail(), token));
  }
}
