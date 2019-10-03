package com.metalr2.service.email;

import com.metalr2.model.email.AbstractEmail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Service
@Profile("prod")
@Slf4j
public class JavaMailEmailService implements EmailService {

  private final JavaMailSender       emailSender;
  private final SpringTemplateEngine templateEngine;
  private final Executor             executor;

  @Autowired
  public JavaMailEmailService(JavaMailSender emailSender, SpringTemplateEngine templateEngine) {
    this.emailSender    = emailSender;
    this.templateEngine = templateEngine;
    this.executor       = Executors.newSingleThreadExecutor();
  }

  @Async
  @Override
  public void sendEmail(AbstractEmail email) {
    executor.execute(() -> {
      MimeMessage mimeMessage = createMimeMessage(email);
      sendEmail(mimeMessage);
    });
  }

  private void sendEmail(MimeMessage mimeMessage) {
    try {
      emailSender.send(mimeMessage);
    }
    catch (MailException me) {
      log.error("unable to send email", me);
    }
  }

  private MimeMessage createMimeMessage(AbstractEmail email) {
    MimeMessage mimeMessage = emailSender.createMimeMessage();
    try {
      MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());
      Context context = new Context();
      context.setVariables(email.getViewModel());
      String html = templateEngine.process(email.getTemplateName(), context);

      helper.setTo(email.getRecipient());
      helper.setText(html, true);
      helper.setSubject(email.getSubject());
      helper.setFrom(email.getFrom());
    }
    catch (MessagingException me) {
      log.error("unable to create email", me);
    }

    return mimeMessage;
  }

}
