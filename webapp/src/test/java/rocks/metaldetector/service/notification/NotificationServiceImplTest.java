package rocks.metaldetector.service.notification;

import org.assertj.core.api.WithAssertions;
import org.assertj.core.data.TemporalUnitLessThanOffset;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rocks.metaldetector.butler.facade.ReleaseService;
import rocks.metaldetector.butler.facade.dto.ReleaseDto;
import rocks.metaldetector.config.constants.ViewNames;
import rocks.metaldetector.persistence.domain.notification.NotificationConfigEntity;
import rocks.metaldetector.persistence.domain.notification.NotificationConfigRepository;
import rocks.metaldetector.persistence.domain.user.UserEntity;
import rocks.metaldetector.security.CurrentUserSupplier;
import rocks.metaldetector.service.artist.FollowArtistService;
import rocks.metaldetector.service.email.AbstractEmail;
import rocks.metaldetector.service.email.EmailService;
import rocks.metaldetector.service.email.TodaysAnnouncementsEmail;
import rocks.metaldetector.service.email.TodaysReleasesEmail;
import rocks.metaldetector.service.user.UserEntityFactory;
import rocks.metaldetector.support.TimeRange;
import rocks.metaldetector.telegram.facade.TelegramService;
import rocks.metaldetector.testutil.DtoFactory.ArtistDtoFactory;
import rocks.metaldetector.testutil.DtoFactory.ReleaseDtoFactory;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static rocks.metaldetector.service.email.ReleasesEmail.SUBJECT;
import static rocks.metaldetector.service.notification.NotificationServiceImpl.SUPPORTED_FREQUENCIES;
import static rocks.metaldetector.service.notification.NotificationServiceImpl.TODAYS_ANNOUNCEMENTS_TEXT;
import static rocks.metaldetector.service.notification.NotificationServiceImpl.TODAYS_RELEASES_TEXT;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest implements WithAssertions {

  @Mock
  private ReleaseService releaseService;

  @Mock
  private EmailService emailService;

  @Mock
  private FollowArtistService followArtistService;

  @Mock
  private NotificationConfigRepository notificationConfigRepository;

  @Mock
  private NotificationConfigTransformer notificationConfigTransformer;

  @Mock
  private CurrentUserSupplier currentUserSupplier;

  @Mock
  private TelegramNotificationFormatter notificationFormatter;

  @Mock
  private TelegramService telegramService;

  @InjectMocks
  private NotificationServiceImpl underTest;

  @AfterEach
  void tearDown() {
    reset(releaseService, emailService, followArtistService, notificationConfigRepository, notificationConfigTransformer,
          currentUserSupplier, notificationConfigRepository, telegramService);
  }

  @DisplayName("Tests for notification on frequency")
  @Nested
  class NotificationFrequencyTest {

    @Captor
    private ArgumentCaptor<TimeRange> timeRangeCaptor;

    @Captor
    private ArgumentCaptor<AbstractEmail> emailCaptor;

    @Captor
    private ArgumentCaptor<NotificationConfigEntity> notificationConfigCaptor;

    private UserEntity userEntity;

    private NotificationConfigEntity notificationConfigEntity;

    @BeforeEach
    void setup() {
      userEntity = UserEntityFactory.createUser("user", "user@user.de");
      notificationConfigEntity = NotificationConfigEntity.builder().user(userEntity).notify(true).telegramChatId(666).build();
      doReturn(List.of(notificationConfigEntity)).when(notificationConfigRepository).findAll();
    }

    @Test
    @DisplayName("FollowArtistService is called on notification")
    void follow_artist_service_is_called() {
      // when
      underTest.notifyOnFrequency();

      // then
      verify(followArtistService).getFollowedArtistsOfUser(userEntity.getPublicId());
    }

    @Test
    @DisplayName("ReleasesService is called for upcoming and recent releases")
    void notify_calls_releases_service() {
      // given
      LocalDate now = LocalDate.now();
      TemporalUnitLessThanOffset offset = new TemporalUnitLessThanOffset(1, ChronoUnit.DAYS);
      when(releaseService.findAllReleases(any(), any())).thenReturn(Collections.emptyList());

      // when
      underTest.notifyOnFrequency();

      // then
      verify(releaseService, times(SUPPORTED_FREQUENCIES.size() * 2)).findAllReleases(eq(Collections.emptyList()), timeRangeCaptor.capture());
      List<TimeRange> capturedRanges = timeRangeCaptor.getAllValues();

      for (int i = 0; i < SUPPORTED_FREQUENCIES.size(); i++) {
        assertThat(capturedRanges.get(i * 2).getDateFrom()).isCloseTo(now, offset);
        assertThat(capturedRanges.get(i * 2).getDateTo()).isCloseTo(now.plusWeeks(SUPPORTED_FREQUENCIES.get(i)), offset);

        assertThat(capturedRanges.get((i * 2) + 1).getDateFrom()).isCloseTo(now.minusWeeks(SUPPORTED_FREQUENCIES.get(i)), offset);
        assertThat(capturedRanges.get((i * 2) + 1).getDateTo()).isCloseTo(now.minusDays(1), offset);
      }
    }

    @Test
    @DisplayName("EmailService is called if releases for followed artists exist")
    void notify_calls_email_service() {
      // given
      var releaseDtos = List.of(ReleaseDtoFactory.withArtistName("A"));
      when(releaseService.findAllReleases(any(), any())).thenReturn(releaseDtos);
      when(followArtistService.getFollowedArtistsOfUser(any())).thenReturn(List.of(ArtistDtoFactory.withName("A")));

      // when
      underTest.notifyOnFrequency();

      // then
      verify(emailService).sendEmail(any());
    }

    @Test
    @DisplayName("Correct email is sent on notification")
    void notify_sends_correct_email() {
      // given
      var releaseDtos = List.of(ReleaseDtoFactory.createDefault());
      when(releaseService.findAllReleases(any(), any())).thenReturn(releaseDtos);
      when(followArtistService.getFollowedArtistsOfUser(any())).thenReturn(List.of(ArtistDtoFactory.createDefault()));

      // when
      underTest.notifyOnFrequency();

      // then
      verify(emailService).sendEmail(emailCaptor.capture());

      AbstractEmail email = emailCaptor.getValue();
      assertThat(email.getRecipient()).isEqualTo(userEntity.getEmail());
      assertThat(email.getSubject()).isEqualTo(SUBJECT);
      assertThat(email.getTemplateName()).isEqualTo(ViewNames.EmailTemplates.NEW_RELEASES);

      List<ReleaseDto> upcomingReleases = (List<ReleaseDto>) email.getEnhancedViewModel("dummy-base-url").get("upcomingReleases");
      List<ReleaseDto> recentReleases = (List<ReleaseDto>) email.getEnhancedViewModel("dummy-base-url").get("recentReleases");
      assertThat(upcomingReleases).isEqualTo(releaseDtos);
      assertThat(recentReleases).isEqualTo(releaseDtos);
    }

    @Test
    @DisplayName("EmailService not called if no releases exist")
    void notify_does_not_call_email_service_no_releases() {
      // given
      when(followArtistService.getFollowedArtistsOfUser(any())).thenReturn(List.of(ArtistDtoFactory.createDefault()));
      when(releaseService.findAllReleases(any(), any())).thenReturn(Collections.emptyList());

      // when
      underTest.notifyOnFrequency();

      // then
      verifyNoInteractions(emailService);
    }

    @Test
    @DisplayName("EmailService is not called if no releases for followed artists exist")
    void notify_does_not_call_email_service_no_releases_for_followed() {
      // given
      var releaseDtos = List.of(ReleaseDtoFactory.withArtistName("B"));
      when(releaseService.findAllReleases(any(), any())).thenReturn(releaseDtos);
      when(followArtistService.getFollowedArtistsOfUser(any())).thenReturn(List.of(ArtistDtoFactory.withName("A")));

      // when
      underTest.notifyOnFrequency();

      // then
      verifyNoInteractions(emailService);
    }

    @Test
    @DisplayName("NotificationConfigRepository is called on notification to get configs")
    void notify_calls_user_service() {
      // when
      underTest.notifyOnFrequency();

      // then
      verify(notificationConfigRepository).findAll();
    }

    @Test
    @DisplayName("Mail- and FollowArtistService are called times the number of active users on notification for all users")
    void notify_calls_services_for_each_user() {
      // given
      var userEntity2 = UserEntityFactory.createUser("user", "user@user.de");
      var notificationConfigEntity2 = NotificationConfigEntity.builder().user(userEntity2).notify(true).build();
      doReturn(List.of(notificationConfigEntity, notificationConfigEntity2)).when(notificationConfigRepository).findAll();
      when(releaseService.findAllReleases(any(), any())).thenReturn(List.of(ReleaseDtoFactory.createDefault()));
      when(followArtistService.getFollowedArtistsOfUser(any())).thenReturn(List.of(ArtistDtoFactory.createDefault()));

      // when
      underTest.notifyOnFrequency();

      // then
      verify(emailService, times(2)).sendEmail(any());
      verify(followArtistService, times(2)).getFollowedArtistsOfUser(any());
    }

    @Test
    @DisplayName("If notifications are deactivated, nothing is called")
    void test_notification_deactivated() {
      // given
      notificationConfigEntity = NotificationConfigEntity.builder().user(userEntity).build();
      doReturn(List.of(notificationConfigEntity)).when(notificationConfigRepository).findAll();

      // when
      underTest.notifyOnFrequency();

      // then
      verifyNoInteractions(followArtistService);
    }

    @Test
    @DisplayName("Nothing is called if notification has already been sent depending on frequency")
    void test_notification_frequency_not_sent() {
      // given
      var now = LocalDate.now();
      var lastNotification = now.minusWeeks(2);
      notificationConfigEntity = NotificationConfigEntity.builder().user(userEntity).lastNotificationDate(lastNotification).notify(true).build();
      doReturn(List.of(notificationConfigEntity)).when(notificationConfigRepository).findAll();

      // when
      underTest.notifyOnFrequency();

      // then
      verifyNoInteractions(followArtistService);
    }

    @Test
    @DisplayName("Notifications are sent if notification has not been sent depending on frequency")
    void test_notification_frequency_sent() {
      // given
      var now = LocalDate.now();
      var lastNotification = now.minusWeeks(4);
      notificationConfigEntity = NotificationConfigEntity.builder().user(userEntity).lastNotificationDate(lastNotification).notify(true).build();
      doReturn(List.of(notificationConfigEntity)).when(notificationConfigRepository).findAll();

      // when
      underTest.notifyOnFrequency();

      // then
      verify(followArtistService).getFollowedArtistsOfUser(any());
    }

    @Test
    @DisplayName("NotificationConfigEntity is updated when email is sent")
    void notify_updates_notification_config() {
      // given
      when(releaseService.findAllReleases(any(), any())).thenReturn(List.of(ReleaseDtoFactory.createDefault()));
      when(followArtistService.getFollowedArtistsOfUser(any())).thenReturn(List.of(ArtistDtoFactory.createDefault()));
      TemporalUnitLessThanOffset offset = new TemporalUnitLessThanOffset(1, ChronoUnit.DAYS);

      // when
      underTest.notifyOnFrequency();

      // then
      verify(notificationConfigRepository).save(notificationConfigCaptor.capture());
      NotificationConfigEntity updatedNotificationConfig = notificationConfigCaptor.getValue();

      assertThat(updatedNotificationConfig.getLastNotificationDate()).isCloseTo(LocalDate.now(), offset);
    }

    @Test
    @DisplayName("Inactive users are not notified")
    void test_inactive_users_not_notified() {
      // given
      userEntity.setEnabled(false);
      notificationConfigEntity = NotificationConfigEntity.builder().user(userEntity).notify(true).build();
      doReturn(List.of(notificationConfigEntity)).when(notificationConfigRepository).findAll();

      // when
      underTest.notifyOnFrequency();

      // then
      verifyNoInteractions(followArtistService);
    }

    @Test
    @DisplayName("if telegramChatId is set, notificationFormatter is called")
    void test_notification_formatter_called() {
      // given
      var releaseDtos = List.of(ReleaseDtoFactory.withArtistName("A"));
      doReturn(releaseDtos).when(releaseService).findAllReleases(any(), any());
      doReturn(List.of(ArtistDtoFactory.withName("A"))).when(followArtistService).getFollowedArtistsOfUser(any());

      // when
      underTest.notifyOnFrequency();

      // then
      verify(notificationFormatter).formatFrequencyNotificationMessage(releaseDtos, releaseDtos);
    }

    @Test
    @DisplayName("if telegramChatId is set, telegramService is called")
    void test_telegram_service_called() {
      // given
      var releaseDtos = List.of(ReleaseDtoFactory.withArtistName("A"));
      var message = "message";
      doReturn(releaseDtos).when(releaseService).findAllReleases(any(), any());
      doReturn(List.of(ArtistDtoFactory.withName("A"))).when(followArtistService).getFollowedArtistsOfUser(any());
      doReturn(message).when(notificationFormatter).formatFrequencyNotificationMessage(anyList(), anyList());

      // when
      underTest.notifyOnFrequency();

      // then
      verify(telegramService).sendMessage(notificationConfigEntity.getTelegramChatId(), message);
    }
  }

  @DisplayName("Tests for notification on release date")
  @Nested
  class NotificationReleaseDateTest {

    @Captor
    private ArgumentCaptor<TimeRange> timeRangeCaptor;

    @Captor
    private ArgumentCaptor<AbstractEmail> emailCaptor;

    private UserEntity userEntity;

    private NotificationConfigEntity notificationConfigEntity;

    @BeforeEach
    void setup() {
      userEntity = UserEntityFactory.createUser("user", "user@user.de");
      notificationConfigEntity = NotificationConfigEntity.builder().user(userEntity).notificationAtReleaseDate(true).telegramChatId(666).build();
      doReturn(List.of(notificationConfigEntity)).when(notificationConfigRepository).findAll();
    }

    @Test
    @DisplayName("FollowArtistService is called on notification")
    void follow_artist_service_is_called() {
      // when
      underTest.notifyOnReleaseDate();

      // then
      verify(followArtistService).getFollowedArtistsOfUser(userEntity.getPublicId());
    }

    @Test
    @DisplayName("ReleasesService is called for today's releases")
    void notify_calls_releases_service() {
      // given
      var artistDto = ArtistDtoFactory.createDefault();
      LocalDate now = LocalDate.now();
      TemporalUnitLessThanOffset offset = new TemporalUnitLessThanOffset(1, ChronoUnit.DAYS);
      when(releaseService.findAllReleases(any(), any())).thenReturn(Collections.emptyList());
      when(followArtistService.getFollowedArtistsOfUser(any())).thenReturn(List.of(artistDto));

      // when
      underTest.notifyOnReleaseDate();

      // then
      verify(releaseService).findAllReleases(eq(Collections.emptyList()), timeRangeCaptor.capture());
      assertThat(timeRangeCaptor.getValue().getDateFrom()).isCloseTo(now, offset);
      assertThat(timeRangeCaptor.getValue().getDateTo()).isCloseTo(now, offset);
    }

    @Test
    @DisplayName("EmailService is called on notification")
    void notify_calls_email_service() {
      // given
      var releaseDtos = List.of(ReleaseDtoFactory.createDefault());
      when(releaseService.findAllReleases(any(), any())).thenReturn(releaseDtos);
      when(followArtistService.getFollowedArtistsOfUser(any())).thenReturn(List.of(ArtistDtoFactory.createDefault()));

      // when
      underTest.notifyOnReleaseDate();

      // then
      verify(emailService).sendEmail(any());
    }

    @Test
    @DisplayName("Correct email is sent on notification")
    void notify_sends_correct_email() {
      // given
      var releaseDtos = List.of(ReleaseDtoFactory.createDefault());
      when(releaseService.findAllReleases(any(), any())).thenReturn(releaseDtos);
      when(followArtistService.getFollowedArtistsOfUser(any())).thenReturn(List.of(ArtistDtoFactory.createDefault()));

      // when
      underTest.notifyOnReleaseDate();

      // then
      verify(emailService).sendEmail(emailCaptor.capture());

      AbstractEmail email = emailCaptor.getValue();
      assertThat(email.getRecipient()).isEqualTo(userEntity.getEmail());
      assertThat(email.getSubject()).isEqualTo(TodaysReleasesEmail.SUBJECT);
      assertThat(email.getTemplateName()).isEqualTo(ViewNames.EmailTemplates.TODAYS_RELEASES);

      List<ReleaseDto> todaysReleases = (List<ReleaseDto>) email.getEnhancedViewModel("dummy-base-url").get("todaysReleases");
      assertThat(todaysReleases).isEqualTo(releaseDtos);
    }

    @Test
    @DisplayName("EmailService not called if no releases exist")
    void notify_does_not_call_email_service() {
      // given
      when(followArtistService.getFollowedArtistsOfUser(any())).thenReturn(List.of(ArtistDtoFactory.createDefault()));
      when(releaseService.findAllReleases(any(), any())).thenReturn(Collections.emptyList());

      // when
      underTest.notifyOnReleaseDate();

      // then
      verifyNoInteractions(emailService);
    }

    @Test
    @DisplayName("EmailService not called if no releases for followed artists exist")
    void notify_does_not_call_email_service_for_followed_artists() {
      // given
      when(followArtistService.getFollowedArtistsOfUser(any())).thenReturn(List.of(ArtistDtoFactory.withName("B")));
      when(releaseService.findAllReleases(any(), any())).thenReturn(List.of(ReleaseDtoFactory.withArtistName("A")));

      // when
      underTest.notifyOnReleaseDate();

      // then
      verifyNoInteractions(emailService);
    }

    @Test
    @DisplayName("NotificationConfigRepository is called on notification to get configs")
    void notify_calls_user_service() {
      // when
      underTest.notifyOnReleaseDate();

      // then
      verify(notificationConfigRepository).findAll();
    }

    @Test
    @DisplayName("Mail- and FollowArtistServices are called times the number of active users")
    void notify_calls_services_for_each_user() {
      // given
      var userEntity2 = UserEntityFactory.createUser("user", "user@user.de");
      var notificationConfigEntity2 = NotificationConfigEntity.builder().user(userEntity2).notify(true).notificationAtReleaseDate(true).build();
      doReturn(List.of(notificationConfigEntity, notificationConfigEntity2)).when(notificationConfigRepository).findAll();
      when(releaseService.findAllReleases(any(), any())).thenReturn(List.of(ReleaseDtoFactory.createDefault()));
      when(followArtistService.getFollowedArtistsOfUser(any())).thenReturn(List.of(ArtistDtoFactory.createDefault()));

      // when
      underTest.notifyOnReleaseDate();

      // then
      verify(emailService, times(2)).sendEmail(any());
      verify(followArtistService, times(2)).getFollowedArtistsOfUser(any());
    }

    @Test
    @DisplayName("If notifications are deactivated, nothing is called")
    void test_notification_deactivated() {
      // given
      notificationConfigEntity = NotificationConfigEntity.builder().user(userEntity).build();
      doReturn(List.of(notificationConfigEntity)).when(notificationConfigRepository).findAll();

      // when
      underTest.notifyOnReleaseDate();

      // then
      verifyNoInteractions(followArtistService);
    }

    @Test
    @DisplayName("If notification at release date is deactivated, nothing is called")
    void test_notification_on_release_date_deactivated() {
      // given
      notificationConfigEntity = NotificationConfigEntity.builder().notify(true).user(userEntity).build();
      doReturn(List.of(notificationConfigEntity)).when(notificationConfigRepository).findAll();

      // when
      underTest.notifyOnReleaseDate();

      // then
      verifyNoInteractions(followArtistService);
    }

    @Test
    @DisplayName("Inactive users are not notified")
    void test_inactive_users_not_notified() {
      // given
      userEntity.setEnabled(false);
      notificationConfigEntity = NotificationConfigEntity.builder().user(userEntity).notify(true).notificationAtReleaseDate(true).build();
      doReturn(List.of(notificationConfigEntity)).when(notificationConfigRepository).findAll();

      // when
      underTest.notifyOnReleaseDate();

      // then
      verifyNoInteractions(followArtistService);
    }

    @Test
    @DisplayName("if telegramChatId is set, notificationFormatter is called")
    void test_notification_formatter_called() {
      // given
      var releaseDtos = List.of(ReleaseDtoFactory.withArtistName("A"));
      doReturn(releaseDtos).when(releaseService).findAllReleases(any(), any());
      doReturn(List.of(ArtistDtoFactory.withName("A"))).when(followArtistService).getFollowedArtistsOfUser(any());

      // when
      underTest.notifyOnReleaseDate();

      // then
      verify(notificationFormatter).formatDateNotificationMessage(releaseDtos, TODAYS_RELEASES_TEXT);
    }

    @Test
    @DisplayName("if telegramChatId is set, telegramService is called")
    void test_telegram_service_called() {
      // given
      var releaseDtos = List.of(ReleaseDtoFactory.withArtistName("A"));
      var message = "message";
      doReturn(releaseDtos).when(releaseService).findAllReleases(any(), any());
      doReturn(List.of(ArtistDtoFactory.withName("A"))).when(followArtistService).getFollowedArtistsOfUser(any());
      doReturn(message).when(notificationFormatter).formatDateNotificationMessage(anyList(), any());

      // when
      underTest.notifyOnReleaseDate();

      // then
      verify(telegramService).sendMessage(notificationConfigEntity.getTelegramChatId(), message);
    }
  }

  @DisplayName("Tests for notification on announcement date")
  @Nested
  class NotificationAnnouncementDateTest {

    @Captor
    private ArgumentCaptor<TimeRange> timeRangeCaptor;

    @Captor
    private ArgumentCaptor<AbstractEmail> emailCaptor;

    private UserEntity userEntity;

    private NotificationConfigEntity notificationConfigEntity;

    @BeforeEach
    void setup() {
      userEntity = UserEntityFactory.createUser("user", "user@user.de");
      notificationConfigEntity = NotificationConfigEntity.builder().user(userEntity).notificationAtAnnouncementDate(true).telegramChatId(666).build();
      doReturn(List.of(notificationConfigEntity)).when(notificationConfigRepository).findAll();
    }

    @Test
    @DisplayName("FollowArtistService is called on notification")
    void follow_artist_service_is_called() {
      // when
      underTest.notifyOnAnnouncementDate();

      // then
      verify(followArtistService).getFollowedArtistsOfUser(userEntity.getPublicId());
    }

    @Test
    @DisplayName("ReleasesService is called for today's releases")
    void notify_calls_releases_service() {
      // given
      var artistDto = ArtistDtoFactory.createDefault();
      LocalDate now = LocalDate.now();
      TemporalUnitLessThanOffset offset = new TemporalUnitLessThanOffset(1, ChronoUnit.DAYS);
      when(releaseService.findAllReleases(any(), any())).thenReturn(Collections.emptyList());
      when(followArtistService.getFollowedArtistsOfUser(any())).thenReturn(List.of(artistDto));

      // when
      underTest.notifyOnAnnouncementDate();

      // then
      verify(releaseService).findAllReleases(eq(Collections.emptyList()), timeRangeCaptor.capture());
      assertThat(timeRangeCaptor.getValue().getDateFrom()).isCloseTo(now, offset);
      assertThat(timeRangeCaptor.getValue().getDateTo()).isNull();
    }

    @Test
    @DisplayName("EmailService is called on notification")
    void notify_calls_email_service() {
      // given
      var releaseDtos = List.of(ReleaseDtoFactory.withAnnouncementDate(LocalDate.now()));
      when(releaseService.findAllReleases(any(), any())).thenReturn(releaseDtos);
      when(followArtistService.getFollowedArtistsOfUser(any())).thenReturn(List.of(ArtistDtoFactory.createDefault()));

      // when
      underTest.notifyOnAnnouncementDate();

      // then
      verify(emailService).sendEmail(any());
    }

    @Test
    @DisplayName("Correct email is sent on notification")
    void notify_sends_correct_email() {
      // given
      var releaseDtos = List.of(ReleaseDtoFactory.withAnnouncementDate(LocalDate.now()));
      when(releaseService.findAllReleases(any(), any())).thenReturn(releaseDtos);
      when(followArtistService.getFollowedArtistsOfUser(any())).thenReturn(List.of(ArtistDtoFactory.createDefault()));

      // when
      underTest.notifyOnAnnouncementDate();

      // then
      verify(emailService).sendEmail(emailCaptor.capture());

      AbstractEmail email = emailCaptor.getValue();
      assertThat(email.getRecipient()).isEqualTo(userEntity.getEmail());
      assertThat(email.getSubject()).isEqualTo(TodaysAnnouncementsEmail.SUBJECT);
      assertThat(email.getTemplateName()).isEqualTo(ViewNames.EmailTemplates.TODAYS_ANNOUNCEMENTS);

      List<ReleaseDto> todaysAnnouncements = (List<ReleaseDto>) email.getEnhancedViewModel("dummy-base-url").get("todaysAnnouncements");
      assertThat(todaysAnnouncements).isEqualTo(releaseDtos);
    }

    @Test
    @DisplayName("EmailService not called if no releases exist")
    void notify_does_not_call_email_service() {
      // given
      when(followArtistService.getFollowedArtistsOfUser(any())).thenReturn(List.of(ArtistDtoFactory.createDefault()));
      when(releaseService.findAllReleases(any(), any())).thenReturn(Collections.emptyList());

      // when
      underTest.notifyOnAnnouncementDate();

      // then
      verifyNoInteractions(emailService);
    }

    @Test
    @DisplayName("EmailService not called if no releases for followed artists exist")
    void notify_does_not_call_email_service_for_followed_artists() {
      // given
      when(followArtistService.getFollowedArtistsOfUser(any())).thenReturn(List.of(ArtistDtoFactory.withName("B")));
      when(releaseService.findAllReleases(any(), any())).thenReturn(List.of(ReleaseDtoFactory.withAnnouncementDate(LocalDate.now())));

      // when
      underTest.notifyOnAnnouncementDate();

      // then
      verifyNoInteractions(emailService);
    }

    @Test
    @DisplayName("EmailService not called if no releases for followed artists with announcement date of today exist")
    void notify_does_not_call_email_service_for_wrong_announcement_date() {
      // given
      when(followArtistService.getFollowedArtistsOfUser(any())).thenReturn(List.of(ArtistDtoFactory.withName("A")));
      when(releaseService.findAllReleases(any(), any())).thenReturn(List.of(ReleaseDtoFactory.withAnnouncementDate(LocalDate.now().minusDays(1))));

      // when
      underTest.notifyOnAnnouncementDate();

      // then
      verifyNoInteractions(emailService);
    }

    @Test
    @DisplayName("NotificationConfigRepository is called on notification to get configs")
    void notify_calls_user_service() {
      // when
      underTest.notifyOnAnnouncementDate();

      // then
      verify(notificationConfigRepository).findAll();
    }

    @Test
    @DisplayName("Mail- and FollowArtistServices are called times the number of active users on notification")
    void notify_calls_services_for_each_user() {
      // given
      var userEntity2 = UserEntityFactory.createUser("user", "user@user.de");
      var notificationConfigEntity2 = NotificationConfigEntity.builder().user(userEntity2).notify(true).notificationAtAnnouncementDate(true).build();
      doReturn(List.of(notificationConfigEntity, notificationConfigEntity2)).when(notificationConfigRepository).findAll();
      when(releaseService.findAllReleases(any(), any())).thenReturn(List.of(ReleaseDtoFactory.withAnnouncementDate(LocalDate.now())));
      when(followArtistService.getFollowedArtistsOfUser(any())).thenReturn(List.of(ArtistDtoFactory.createDefault()));

      // when
      underTest.notifyOnAnnouncementDate();

      // then
      verify(emailService, times(2)).sendEmail(any());
      verify(followArtistService, times(2)).getFollowedArtistsOfUser(any());
    }

    @Test
    @DisplayName("If notifications are deactivated, nothing is called")
    void test_notification_deactivated() {
      // given
      notificationConfigEntity = NotificationConfigEntity.builder().user(userEntity).build();
      doReturn(List.of(notificationConfigEntity)).when(notificationConfigRepository).findAll();

      // when
      underTest.notifyOnAnnouncementDate();

      // then
      verifyNoInteractions(followArtistService);
    }

    @Test
    @DisplayName("If notification at announcement date is deactivated, nothing is called")
    void test_notification_on_release_date_deactivated() {
      // given
      notificationConfigEntity = NotificationConfigEntity.builder().notify(true).user(userEntity).build();
      doReturn(List.of(notificationConfigEntity)).when(notificationConfigRepository).findAll();

      // when
      underTest.notifyOnAnnouncementDate();

      // then
      verifyNoInteractions(followArtistService);
    }

    @Test
    @DisplayName("Inactive users are not notified")
    void test_inactive_users_not_notified() {
      // given
      userEntity.setEnabled(false);
      notificationConfigEntity = NotificationConfigEntity.builder().user(userEntity).notify(true).notificationAtReleaseDate(true).build();
      doReturn(List.of(notificationConfigEntity)).when(notificationConfigRepository).findAll();

      // when
      underTest.notifyOnAnnouncementDate();

      // then
      verifyNoInteractions(followArtistService);
    }

    @Test
    @DisplayName("if telegramChatId is set, notificationFormatter is called")
    void test_notification_formatter_called() {
      // given
      var releaseDtos = List.of(ReleaseDtoFactory.withAnnouncementDate(LocalDate.now()));
      doReturn(releaseDtos).when(releaseService).findAllReleases(any(), any());
      doReturn(List.of(ArtistDtoFactory.withName("A"))).when(followArtistService).getFollowedArtistsOfUser(any());

      // when
      underTest.notifyOnAnnouncementDate();

      // then
      verify(notificationFormatter).formatDateNotificationMessage(releaseDtos, TODAYS_ANNOUNCEMENTS_TEXT);
    }

    @Test
    @DisplayName("if telegramChatId is set, telegramService is called")
    void test_telegram_service_called() {
      // given
      var releaseDtos = List.of(ReleaseDtoFactory.withAnnouncementDate(LocalDate.now()));
      var message = "message";
      doReturn(releaseDtos).when(releaseService).findAllReleases(any(), any());
      doReturn(List.of(ArtistDtoFactory.withName("A"))).when(followArtistService).getFollowedArtistsOfUser(any());
      doReturn(message).when(notificationFormatter).formatDateNotificationMessage(anyList(), any());

      // when
      underTest.notifyOnAnnouncementDate();

      // then
      verify(telegramService).sendMessage(notificationConfigEntity.getTelegramChatId(), message);
    }
  }
}
