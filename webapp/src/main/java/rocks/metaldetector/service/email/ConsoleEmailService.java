package rocks.metaldetector.service.email;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import rocks.metaldetector.config.misc.MailConfig;

@Service
@Slf4j
@Profile("default")
@AllArgsConstructor
public class ConsoleEmailService implements EmailService {

  private final SpringTemplateEngine templateEngine;
  private final MailConfig mailConfig;

  @Override
  public void sendEmail(AbstractEmail email) {
    Context context = new Context();
    String baseUrl = mailConfig.getApplicationHostUrl() + ":" + mailConfig.getApplicationPort();
    context.setVariables(email.getEnhancedViewModel(baseUrl));
    String messageAsHtml = templateEngine.process(email.getTemplateName(), context);

    log.debug("From: {}", mailConfig.getFromEmail());
    log.debug("Recipient: {}", email.getRecipient());
    log.debug("Subject: {}", email.getSubject());
    log.debug("Message as Html: {}", messageAsHtml);
  }
}
