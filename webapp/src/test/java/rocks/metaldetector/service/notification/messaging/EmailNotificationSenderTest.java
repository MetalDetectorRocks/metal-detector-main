package rocks.metaldetector.service.notification.messaging;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rocks.metaldetector.butler.facade.dto.ReleaseDto;
import rocks.metaldetector.config.constants.ViewNames;
import rocks.metaldetector.persistence.domain.user.AbstractUserEntity;
import rocks.metaldetector.service.email.EmailService;
import rocks.metaldetector.service.email.ReleasesEmail;
import rocks.metaldetector.service.email.TodaysAnnouncementsEmail;
import rocks.metaldetector.service.email.TodaysReleasesEmail;
import rocks.metaldetector.service.user.UserEntityFactory;
import rocks.metaldetector.testutil.DtoFactory.ReleaseDtoFactory;

import java.util.List;

import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EmailNotificationSenderTest implements WithAssertions {

  private static final AbstractUserEntity USER = UserEntityFactory.createUser("user", "user@user.user");

  @Mock
  private EmailService emailService;

  @InjectMocks
  private EmailNotificationSender underTest;

  @AfterEach
  void tearDown() {
    reset(emailService);
  }

  @Test
  @DisplayName("sendFrequencyMessage: emailService is called if releases are present")
  void test_frequency_email_service_called() {
    // given
    ArgumentCaptor<ReleasesEmail> argumentCaptor = ArgumentCaptor.forClass(ReleasesEmail.class);
    var releases = List.of(ReleaseDtoFactory.createDefault());

    // when
    underTest.sendFrequencyMessage(USER, releases, releases);

    // then
    verify(emailService).sendEmail(argumentCaptor.capture());
    var mail = argumentCaptor.getValue();
    assertThat(mail.getRecipient()).isEqualTo(USER.getEmail());
    assertThat(mail.getSubject()).isEqualTo(ReleasesEmail.SUBJECT);
    assertThat(mail.getTemplateName()).isEqualTo(ViewNames.EmailTemplates.NEW_RELEASES);

    var upcomingReleases = (List<ReleaseDto>) mail.createViewModel("dummy-base-url").get("upcomingReleases");
    var recentReleases = (List<ReleaseDto>) mail.createViewModel("dummy-base-url").get("recentReleases");
    var username = (String) mail.createViewModel("dummy-base-url").get("username");
    assertThat(username).isEqualTo(USER.getUsername());
    assertThat(upcomingReleases).isEqualTo(releases);
    assertThat(recentReleases).isEqualTo(releases);
  }

  @Test
  @DisplayName("sendReleaseDateMessage: emailService is called if releases are present")
  void test_release_date_email_service_called() {
    // given
    ArgumentCaptor<TodaysReleasesEmail> argumentCaptor = ArgumentCaptor.forClass(TodaysReleasesEmail.class);
    var releases = List.of(ReleaseDtoFactory.createDefault());

    // when
    underTest.sendReleaseDateMessage(USER, releases);

    // then
    verify(emailService).sendEmail(argumentCaptor.capture());
    var mail = argumentCaptor.getValue();
    assertThat(mail.getRecipient()).isEqualTo(USER.getEmail());
    assertThat(mail.getSubject()).isEqualTo(TodaysReleasesEmail.SUBJECT);
    assertThat(mail.getTemplateName()).isEqualTo(ViewNames.EmailTemplates.TODAYS_RELEASES);

    var todaysReleases = (List<ReleaseDto>) mail.createViewModel("dummy-base-url").get("todaysReleases");
    var username = (String) mail.createViewModel("dummy-base-url").get("username");
    assertThat(username).isEqualTo(USER.getUsername());
    assertThat(todaysReleases).isEqualTo(releases);
  }

  @Test
  @DisplayName("sendAnnouncementDateMessage: emailService is called if releases are present")
  void test_announcement_date_email_service_called() {
    // given
    ArgumentCaptor<TodaysAnnouncementsEmail> argumentCaptor = ArgumentCaptor.forClass(TodaysAnnouncementsEmail.class);
    var releases = List.of(ReleaseDtoFactory.createDefault());

    // when
    underTest.sendAnnouncementDateMessage(USER, releases);

    // then
    verify(emailService).sendEmail(argumentCaptor.capture());
    var mail = argumentCaptor.getValue();
    assertThat(mail.getRecipient()).isEqualTo(USER.getEmail());
    assertThat(mail.getSubject()).isEqualTo(TodaysAnnouncementsEmail.SUBJECT);
    assertThat(mail.getTemplateName()).isEqualTo(ViewNames.EmailTemplates.TODAYS_ANNOUNCEMENTS);

    var todaysAnnouncements = (List<ReleaseDto>) mail.createViewModel("dummy-base-url").get("todaysAnnouncements");
    var username = (String) mail.createViewModel("dummy-base-url").get("username");
    assertThat(username).isEqualTo(USER.getUsername());
    assertThat(todaysAnnouncements).isEqualTo(releases);
  }
}
