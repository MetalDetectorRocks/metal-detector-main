package rocks.metaldetector.service.notification.messaging;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
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

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

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

  @DisplayName("Tests for notification on frequency")
  @Nested
  class NotificationFrequencyTest {

    @Captor
    private ArgumentCaptor<ReleasesEmail> argumentCaptor;

    @Test
    @DisplayName("emailService is called if releases are present")
    void test_email_service_called() {
      // given
      var releases = List.of(ReleaseDtoFactory.createDefault());

      // when
      underTest.sendFrequencyMessage(USER, releases, releases);

      // then
      verify(emailService).sendEmail(argumentCaptor.capture());
      var mail = argumentCaptor.getValue();
      assertThat(mail.getRecipient()).isEqualTo(USER.getEmail());
      assertThat(mail.getSubject()).isEqualTo(ReleasesEmail.SUBJECT);
      assertThat(mail.getTemplateName()).isEqualTo(ViewNames.EmailTemplates.NEW_RELEASES);

      var upcomingReleases = (List<ReleaseDto>) mail.getEnhancedViewModel("dummy-base-url").get("upcomingReleases");
      var recentReleases = (List<ReleaseDto>) mail.getEnhancedViewModel("dummy-base-url").get("recentReleases");
      var username = (String) mail.getEnhancedViewModel("dummy-base-url").get("username");
      assertThat(username).isEqualTo(USER.getUsername());
      assertThat(upcomingReleases).isEqualTo(releases);
      assertThat(recentReleases).isEqualTo(releases);
    }

    @Test
    @DisplayName("emailService is not called if not releases are present")
    void test_email_service_not_called() {
      // when
      underTest.sendFrequencyMessage(USER, Collections.emptyList(), Collections.emptyList());

      // then
      verifyNoInteractions(emailService);
    }
  }

  @DisplayName("Tests for notification on release date")
  @Nested
  class NotificationReleaseDateTest {

    @Captor
    private ArgumentCaptor<TodaysReleasesEmail> argumentCaptor;

    @Test
    @DisplayName("emailService is called if releases are present")
    void test_email_service_called() {
      // given
      var releases = List.of(ReleaseDtoFactory.createDefault());

      // when
      underTest.sendReleaseDateMessage(USER, releases);

      // then
      verify(emailService).sendEmail(argumentCaptor.capture());
      var mail = argumentCaptor.getValue();
      assertThat(mail.getRecipient()).isEqualTo(USER.getEmail());
      assertThat(mail.getSubject()).isEqualTo(TodaysReleasesEmail.SUBJECT);
      assertThat(mail.getTemplateName()).isEqualTo(ViewNames.EmailTemplates.TODAYS_RELEASES);

      var todaysReleases = (List<ReleaseDto>) mail.getEnhancedViewModel("dummy-base-url").get("todaysReleases");
      var username = (String) mail.getEnhancedViewModel("dummy-base-url").get("username");
      assertThat(username).isEqualTo(USER.getUsername());
      assertThat(todaysReleases).isEqualTo(releases);
    }

    @Test
    @DisplayName("emailService is not called if not releases are present")
    void test_email_service_not_called() {
      // when
      underTest.sendReleaseDateMessage(USER, Collections.emptyList());

      // then
      verifyNoInteractions(emailService);
    }
  }

  @DisplayName("Tests for notification on announcement date")
  @Nested
  class NotificationAnnouncementDateTest {

    @Captor
    private ArgumentCaptor<TodaysAnnouncementsEmail> argumentCaptor;

    @Test
    @DisplayName("emailService is called if releases are present")
    void test_email_service_called() {
      // given
      var releases = List.of(ReleaseDtoFactory.createDefault());

      // when
      underTest.sendAnnouncementDateMessage(USER, releases);

      // then
      verify(emailService).sendEmail(argumentCaptor.capture());
      var mail = argumentCaptor.getValue();
      assertThat(mail.getRecipient()).isEqualTo(USER.getEmail());
      assertThat(mail.getSubject()).isEqualTo(TodaysAnnouncementsEmail.SUBJECT);
      assertThat(mail.getTemplateName()).isEqualTo(ViewNames.EmailTemplates.TODAYS_ANNOUNCEMENTS);

      var todaysAnnouncements = (List<ReleaseDto>) mail.getEnhancedViewModel("dummy-base-url").get("todaysAnnouncements");
      var username = (String) mail.getEnhancedViewModel("dummy-base-url").get("username");
      assertThat(username).isEqualTo(USER.getUsername());
      assertThat(todaysAnnouncements).isEqualTo(releases);
    }

    @Test
    @DisplayName("emailService is not called if not releases are present")
    void test_email_service_not_called() {
      // when
      underTest.sendAnnouncementDateMessage(USER, Collections.emptyList());

      // then
      verifyNoInteractions(emailService);
    }
  }
}
