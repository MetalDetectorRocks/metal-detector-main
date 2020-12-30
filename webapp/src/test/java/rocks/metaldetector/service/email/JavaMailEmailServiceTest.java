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
import org.springframework.mail.javamail.MimeMessageHelper;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.ISpringTemplateEngine;
import rocks.metaldetector.config.misc.MailProperties;
import rocks.metaldetector.support.Endpoints;
import rocks.metaldetector.testutil.CurrentThreadExecutor;

import javax.mail.internet.MimeMessage;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class JavaMailEmailServiceTest implements WithAssertions {

  @Mock
  private JavaMailSender emailSender;

  @Mock
  private ISpringTemplateEngine templateEngine;

  @Mock
  private MailProperties mailProperties;

  @Mock
  private MimeMessageHelperSupplier messageHelperSupplier;

  @Mock
  private MimeMessageHelper mimeMessageHelperMock;

  @InjectMocks
  private JavaMailEmailService underTest;

  @BeforeEach
  void setUp() {
    underTest.setExecutor(new CurrentThreadExecutor());
    underTest.setMessageHelperSupplier(messageHelperSupplier);
    doReturn(mimeMessageHelperMock).when(messageHelperSupplier).apply(any());
  }

  @AfterEach
  void tearDown() {
    reset(emailSender, templateEngine, mailProperties);
  }

  @Test
  @DisplayName("Should call createMimeMessage on JavaMailSender")
  void should_call_create_mime_message() {
    // given
    AbstractEmail email = new ForgotPasswordEmail("john.doe@example.com", "user", "token");

    // when
    underTest.sendEmail(email);

    // then
    verify(emailSender).createMimeMessage();
  }

  @Test
  @DisplayName("Should call TemplateEngine")
  void should_call_template_engine() {
    // given
    final String TOKEN = "token";
    final String EXPECTED_VERIFICATION_URL = mailProperties.getApplicationHostUrl() + Endpoints.Guest.REGISTRATION_VERIFICATION + "?token=" + TOKEN;
    ArgumentCaptor<Context> contextCaptor = ArgumentCaptor.forClass(Context.class);
    ArgumentCaptor<String> templateNameCaptor = ArgumentCaptor.forClass(String.class);
    AbstractEmail email = new RegistrationVerificationEmail("john.doe@example.com", "username", TOKEN);

    // when
    underTest.sendEmail(email);

    // then
    verify(templateEngine).process(templateNameCaptor.capture(), contextCaptor.capture());
    assertThat(templateNameCaptor.getValue()).isEqualTo(email.getTemplateName());
    assertThat(contextCaptor.getValue().getVariable("verificationUrl")).isEqualTo(EXPECTED_VERIFICATION_URL);
  }

  @Test
  @DisplayName("Should set email data")
  void should_set_email_data() throws Exception {
    // given
    AbstractEmail email = new RegistrationVerificationEmail("john.doe@example.com", "username", "token");
    String emailAsHtml = "<h1>Test</h1>";
    String fromEmail = "from@example.de";
    String fromName = "Example";
    String applicationUrl = "localhost";
    doReturn(emailAsHtml).when(templateEngine).process(anyString(), any(Context.class));
    doReturn(fromEmail).when(mailProperties).getFromEmail();
    doReturn(fromName).when(mailProperties).getFromName();
    doReturn(applicationUrl).when(mailProperties).getApplicationHostUrl();

    // when
    underTest.sendEmail(email);

    // then
    verify(mimeMessageHelperMock).setTo(email.getRecipient());
    verify(mimeMessageHelperMock).setText(emailAsHtml, true);
    verify(mimeMessageHelperMock).setSubject(email.getSubject());
    verify(mimeMessageHelperMock).setFrom(fromEmail, fromName);
    verify(mimeMessageHelperMock).setReplyTo(fromEmail);
  }

  @Test
  @DisplayName("Should send mime message via emailSender")
  void should_send_message() {
    // given
    AbstractEmail email = new RegistrationVerificationEmail("john.doe@example.com", "username", "token");
    MimeMessage mimeMessageMock = mock(MimeMessage.class);
    doReturn(mimeMessageMock).when(emailSender).createMimeMessage();

    // when
    underTest.sendEmail(email);

    // then
    verify(emailSender).send(mimeMessageMock);
  }
}
