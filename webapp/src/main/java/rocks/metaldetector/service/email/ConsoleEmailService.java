package rocks.metaldetector.service.email;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import rocks.metaldetector.config.misc.MailProperties;

@Service
@Slf4j
@Profile({"default", "mockmode"})
@AllArgsConstructor
public class ConsoleEmailService implements EmailService {

  private final SpringTemplateEngine templateEngine;
  private final MailProperties mailProperties;

  @Override
  public void sendEmail(AbstractEmail email) {
    Context context = new Context();
    String baseUrl = mailProperties.getApplicationHostUrl() + ":" + mailProperties.getApplicationPort();
    context.setVariables(email.getEnhancedViewModel(baseUrl));
    String messageAsHtml = templateEngine.process(email.getTemplateName(), context);

    log.debug("From: {}", mailProperties.getFromEmail());
    log.debug("Recipient: {}", email.getRecipient());
    log.debug("Subject: {}", email.getSubject());
    log.debug("Message as Html: {}", messageAsHtml);
  }
}
