package rocks.metaldetector.service.email;

import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.function.Function;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.mail.javamail.MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED;

@Slf4j
@Component
public class MimeMessageHelperFunction implements Function<MimeMessage, MimeMessageHelper> {

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
