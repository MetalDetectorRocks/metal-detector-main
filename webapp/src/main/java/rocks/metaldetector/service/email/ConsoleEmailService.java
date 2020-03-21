package rocks.metaldetector.service.email;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import rocks.metaldetector.config.misc.MailConfig;

@Service
@Slf4j
@Profile("!prod")
public class ConsoleEmailService implements EmailService {

  private final SpringTemplateEngine templateEngine;
  private final MailConfig mailConfig;

  @Autowired
  public ConsoleEmailService(SpringTemplateEngine templateEngine, MailConfig mailConfig) {
    this.templateEngine = templateEngine;
    this.mailConfig = mailConfig;
  }

  @Override
  public void sendEmail(AbstractEmail email) {
    Context context = new Context();
    context.setVariables(email.getEnhancedViewModel(mailConfig.getHost()));
    String messageAsHtml = templateEngine.process(email.getTemplateName(), context);

    log.debug("From: {}", mailConfig.getFromEmail());
    log.debug("Recipient: {}", email.getRecipient());
    log.debug("Subject: {}", email.getSubject());
    log.debug("Message as Html: {}", messageAsHtml);
  }

}
