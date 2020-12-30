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
import rocks.metaldetector.config.misc.MailProperties;

import javax.annotation.PostConstruct;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Function;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.mail.javamail.MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED;

@Service
@Slf4j
@Profile({"preview", "prod"})
@AllArgsConstructor
public class JavaMailEmailService implements EmailService {

  private final JavaMailSender emailSender;
  private final ISpringTemplateEngine templateEngine;
  private final MailProperties mailProperties;
  private MimeMessageHelperSupplier messageHelperSupplier;
  private Executor executor;

  @PostConstruct
  private void init() {
    this.executor = Executors.newSingleThreadExecutor();
    this.messageHelperSupplier = new MimeMessageHelperSupplier();
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
    context.setVariables(email.getEnhancedViewModel(mailProperties.getApplicationHostUrl()));
    String html = templateEngine.process(email.getTemplateName(), context);

    try {
      MimeMessageHelper helper = messageHelperSupplier.apply(mimeMessage);
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

  void setExecutor(Executor executor) {
    this.executor = executor;
  }
}

@Slf4j
class MimeMessageHelperSupplier implements Function<MimeMessage, MimeMessageHelper> {

  @Override
  public MimeMessageHelper apply(MimeMessage mimeMessage) {
    try {
      return new MimeMessageHelper(mimeMessage, MULTIPART_MODE_MIXED_RELATED, UTF_8.name());
    }
    catch (MessagingException me) {
      log.error("unable to create mime message helper", me);
      throw new RuntimeException("snh: unable to create mime message helper");
    }
  }
}