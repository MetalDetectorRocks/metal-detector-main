package rocks.metaldetector.service.email;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.ISpringTemplateEngine;
import rocks.metaldetector.config.misc.MailConfig;

import javax.annotation.PostConstruct;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Service
@Slf4j
@Profile({"preview", "prod"})
@AllArgsConstructor
public class JavaMailEmailService implements EmailService {

  private final JavaMailSender emailSender;
  private final ISpringTemplateEngine templateEngine;
  private final MailConfig mailConfig;
  private Executor executor;

  @PostConstruct
  private void init() {
    this.executor = Executors.newSingleThreadExecutor();
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
      log.error("Unable to send email", me);
    }
  }

  private MimeMessage createMimeMessage(AbstractEmail email) {
    MimeMessage mimeMessage = emailSender.createMimeMessage();
    Context context = new Context();
    context.setVariables(email.getEnhancedViewModel(mailConfig.getHost()));
    String html = templateEngine.process(email.getTemplateName(), context);

    try {
      MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());
      helper.setTo(email.getRecipient());
      helper.setText(html, true);
      helper.setSubject(email.getSubject());
      helper.setReplyTo(mailConfig.getFromEmail());
    }
    catch (MessagingException me) {
      log.error("Unable to create email", me);
    }

    return mimeMessage;
  }

  void setExecutor(Executor executor) {
    this.executor = executor;
  }
}
