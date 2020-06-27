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
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import rocks.metaldetector.butler.facade.dto.ReleaseDto;
import rocks.metaldetector.config.misc.MailProperties;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static rocks.metaldetector.testutil.DtoFactory.ReleaseDtoFactory;

@ExtendWith(MockitoExtension.class)
class ConsoleEmailServiceTest implements WithAssertions {

  @Mock
  private SpringTemplateEngine templateEngine;

  @Mock
  private MailProperties mailProperties;

  @InjectMocks
  private ConsoleEmailService emailService;

  @BeforeEach
  void setUp() {
    when(mailProperties.getFromEmail()).thenReturn("from@example.de");
    when(templateEngine.process(anyString(), any(Context.class))).thenReturn("<h1>Test</h1>");
  }

  @AfterEach
  void tearDown() {
    reset(templateEngine, mailProperties);
  }

  @Test
  @DisplayName("Sending an email to Console should interact with TemplateEngine as expected")
  void send_email_should_use_template_engine() {
    // given
    List<ReleaseDto> releases = List.of(ReleaseDtoFactory.createDefault());
    ArgumentCaptor<Context> contextCaptor = ArgumentCaptor.forClass(Context.class);
    ArgumentCaptor<String> templateNameCaptor = ArgumentCaptor.forClass(String.class);
    AbstractEmail email = new NewReleasesEmail("john.doe@example.com", "JohnDoe", releases);

    // when
    emailService.sendEmail(email);

    // then
    verify(templateEngine, times(1)).process(templateNameCaptor.capture(), contextCaptor.capture());

    assertThat(templateNameCaptor.getValue()).isEqualTo(email.getTemplateName());
    assertThat(contextCaptor.getValue().getVariable("newReleases")).isEqualTo(releases);
  }

  @Test
  @DisplayName("Sending an email to Console should interact with MailConfig as expected")
  void send_email_should_use_mail_config() {
    // given
    AbstractEmail email = new NewReleasesEmail("john.doe@example.com", "JohnDoe", Collections.emptyList());

    // when
    emailService.sendEmail(email);

    // then
    verify(mailProperties, times(1)).getFromEmail();
  }
}