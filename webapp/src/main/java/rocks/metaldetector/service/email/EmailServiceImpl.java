package rocks.metaldetector.service.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.ISpringTemplateEngine;
import rocks.metaldetector.config.misc.MailProperties;

import java.io.UnsupportedEncodingException;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

  private final JavaMailSender emailSender;
  private final ISpringTemplateEngine templateEngine;
  private final MailProperties mailProperties;
  private final MimeMessageHelperFunction messageHelperFunction;

  @Setter
  @Value("${frontend.origin}")
  private String frontendBaseUrl;

  @Async
  @Override
  public void sendEmail(Email email) {
    MimeMessage mimeMessage = createMimeMessage(email);
    sendEmail(mimeMessage);
  }

  private void sendEmail(MimeMessage mimeMessage) {
    try {
      emailSender.send(mimeMessage);
    }
    catch (MailException me) {
      log.error("Unable to send email", me);
    }
  }

  private MimeMessage createMimeMessage(Email email) {
    MimeMessage mimeMessage = emailSender.createMimeMessage();
    Context context = new Context();
    context.setVariables(email.createViewModel(frontendBaseUrl));
    String html = templateEngine.process(email.getTemplateName(), context);

    try {
      MimeMessageHelper helper = messageHelperFunction.apply(mimeMessage);
      helper.setTo(email.getRecipient());
      helper.setText(html, true);
      helper.setSubject(email.getSubject());
      helper.setFrom(mailProperties.getFromEmail(), mailProperties.getFromName());
      helper.setReplyTo(mailProperties.getFromEmail());
    }
    catch (MessagingException | UnsupportedEncodingException e) {
      log.error("Unable to create email", e);
    }

    return mimeMessage;
  }
}
