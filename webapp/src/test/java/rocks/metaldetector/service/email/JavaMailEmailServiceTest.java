package rocks.metaldetector.service.email;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.ISpringTemplateEngine;
import rocks.metaldetector.config.constants.Endpoints;
import rocks.metaldetector.config.misc.MailConfig;
import rocks.metaldetector.testutil.CurrentThreadExecutor;

import javax.mail.internet.MimeMessage;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JavaMailEmailServiceTest implements WithAssertions {

  @Mock
  private JavaMailSender emailSender;

  @Mock
  private ISpringTemplateEngine templateEngine;

  @Mock
  private MailConfig mailConfig;

  @InjectMocks
  private JavaMailEmailService emailService;

  @BeforeEach
  void setUp() {
    // set thread executor
    emailService.setExecutor(new CurrentThreadExecutor());

    // mock mail config
    when(mailConfig.getFromEmail()).thenReturn("from@example.de");
    when(mailConfig.getHost()).thenReturn("localhost:1234");

    // mock emailSender
    MimeMessage mimeMessage = mock(MimeMessage.class);
    when(emailSender.createMimeMessage()).thenReturn(mimeMessage);

    // mock templateEngine
    when(templateEngine.process(anyString(), any(Context.class))).thenReturn("<h1>Test</h1>");
  }

  @AfterEach
  void tearDown() {
    reset(emailSender, templateEngine, mailConfig);
  }

  @Test
  @DisplayName("Sending an email with JavaMail should interact with EmailSender and TemplateEngine as expected")
  void send_email() {
    // given
    final String TOKEN = "token";
    final String EXPECTED_VERIFICATION_URL = mailConfig.getHost() + Endpoints.Guest.REGISTRATION_VERIFICATION + "?token=" + TOKEN;
    ArgumentCaptor<Context> contextCaptor = ArgumentCaptor.forClass(Context.class);
    ArgumentCaptor<String> templateNameCaptor = ArgumentCaptor.forClass(String.class);
    AbstractEmail email = new RegistrationVerificationEmail("john.doe@example.com", TOKEN);

    // when
    emailService.sendEmail(email);

    // then
    verify(emailSender, times(1)).createMimeMessage();
    verify(templateEngine, times(1)).process(templateNameCaptor.capture(), contextCaptor.capture());
    verify(emailSender, times(1)).send(any(MimeMessage.class));

    assertThat(templateNameCaptor.getValue()).isEqualTo(email.getTemplateName());
    assertThat(contextCaptor.getValue().getVariable("verificationUrl")).isEqualTo(EXPECTED_VERIFICATION_URL);
  }
}
