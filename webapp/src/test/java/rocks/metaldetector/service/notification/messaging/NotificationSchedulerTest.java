package rocks.metaldetector.service.notification.messaging;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import rocks.metaldetector.persistence.domain.notification.NotificationConfigEntity;
import rocks.metaldetector.persistence.domain.notification.NotificationConfigRepository;
import rocks.metaldetector.persistence.domain.user.AbstractUserEntity;
import rocks.metaldetector.service.user.UserEntityFactory;
import rocks.metaldetector.testutil.DtoFactory.ReleaseDtoFactory;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static rocks.metaldetector.persistence.domain.notification.NotificationChannel.EMAIL;
import static rocks.metaldetector.persistence.domain.notification.NotificationChannel.TELEGRAM;

@ExtendWith(MockitoExtension.class)
class NotificationSchedulerTest implements WithAssertions {

  private static final AbstractUserEntity USER = UserEntityFactory.createUser("user", "mail@mail.mail");

  @Mock
  private NotificationConfigRepository notificationConfigRepository;

  @Mock
  private NotificationReleaseCollector notificationReleaseCollector;

  @Mock
  private NotificationSenderSupplier notificationSenderSupplier;

  @InjectMocks
  private NotificationScheduler underTest;

  @AfterEach
  void tearDown() {
    reset(notificationConfigRepository, notificationReleaseCollector, notificationSenderSupplier);
  }

  @Nested
  @DisplayName("Tests for notification on frequency")
  class FrequencyTests {

    private final NotificationConfigEntity notificationConfig = NotificationConfigEntity.builder()
        .user(USER)
        .channel(EMAIL)
        .frequencyInWeeks(2)
        .notify(true)
        .build();

    @Test
    @DisplayName("repository is called")
    void test_repository_called() {
      // when
      underTest.notifyOnFrequency();

      // then
      verify(notificationConfigRepository).findAllActive();
    }

    @Test
    @DisplayName("releaseCollector is called")
    void test_release_collector_called() {
      // given
      doReturn(List.of(notificationConfig)).when(notificationConfigRepository).findAllActive();
      doReturn(mock(NotificationSender.class)).when(notificationSenderSupplier).apply(any());
      doReturn(new NotificationReleaseCollector.ReleaseContainer(Collections.emptyList(), Collections.emptyList()))
          .when(notificationReleaseCollector).fetchReleasesForUserAndFrequency(any(), anyInt());

      // when
      underTest.notifyOnFrequency();

      // then
      verify(notificationReleaseCollector).fetchReleasesForUserAndFrequency(notificationConfig.getUser(), notificationConfig.getFrequencyInWeeks());
    }

    @Test
    @DisplayName("serviceSupplier is called")
    void test_service_supplier_called() {
      // given
      doReturn(List.of(notificationConfig)).when(notificationConfigRepository).findAllActive();
      doReturn(mock(NotificationSender.class)).when(notificationSenderSupplier).apply(any());
      doReturn(new NotificationReleaseCollector.ReleaseContainer(Collections.emptyList(), Collections.emptyList()))
          .when(notificationReleaseCollector).fetchReleasesForUserAndFrequency(any(), anyInt());

      // when
      underTest.notifyOnFrequency();

      // then
      verify(notificationSenderSupplier).apply(notificationConfig.getChannel());
    }

    @Test
    @DisplayName("message is sent with notificationService")
    void test_message_sent() {
      // given
      var notificationServiceMock = mock(NotificationSender.class);
      var upcomingReleases = List.of(ReleaseDtoFactory.withArtistName("A"));
      var recentReleases = List.of(ReleaseDtoFactory.withArtistName("B"));
      doReturn(List.of(notificationConfig)).when(notificationConfigRepository).findAllActive();
      doReturn(notificationServiceMock).when(notificationSenderSupplier).apply(any());
      doReturn(new NotificationReleaseCollector.ReleaseContainer(upcomingReleases, recentReleases))
          .when(notificationReleaseCollector).fetchReleasesForUserAndFrequency(any(), anyInt());

      // when
      underTest.notifyOnFrequency();

      // then
      verify(notificationServiceMock).sendFrequencyMessage(notificationConfig.getUser(), upcomingReleases, recentReleases);
    }

    @Test
    @DisplayName("notificationDate is saved")
    void test_notification_date_saved() {
      // given
      ArgumentCaptor<NotificationConfigEntity> argumentCaptor = ArgumentCaptor.forClass(NotificationConfigEntity.class);
      doReturn(List.of(notificationConfig)).when(notificationConfigRepository).findAllActive();
      doReturn(mock(NotificationSender.class)).when(notificationSenderSupplier).apply(any());
      doReturn(new NotificationReleaseCollector.ReleaseContainer(Collections.emptyList(), Collections.emptyList()))
          .when(notificationReleaseCollector).fetchReleasesForUserAndFrequency(any(), anyInt());

      // when
      LocalDate now = LocalDate.of(2000, 1, 1);
      try (MockedStatic<LocalDate> mock = mockStatic(LocalDate.class)) {
        mock.when(LocalDate::now).thenReturn(now);
        underTest.notifyOnFrequency();
      }

      // then
      verify(notificationConfigRepository).save(argumentCaptor.capture());
      var savedConfig = argumentCaptor.getValue();
      assertThat(savedConfig.getLastNotificationDate()).isEqualTo(now);
    }

    @Test
    @DisplayName("services are called for each active config")
    void test_services_called_for_each_config() {
      // given
      var notificationConfig2 = NotificationConfigEntity.builder().user(USER).notify(true).channel(TELEGRAM).build();
      doReturn(List.of(notificationConfig, notificationConfig2)).when(notificationConfigRepository).findAllActive();
      doReturn(mock(NotificationSender.class)).when(notificationSenderSupplier).apply(any());
      doReturn(new NotificationReleaseCollector.ReleaseContainer(Collections.emptyList(), Collections.emptyList()))
          .when(notificationReleaseCollector).fetchReleasesForUserAndFrequency(any(), anyInt());

      // when
      underTest.notifyOnFrequency();

      // then
      verify(notificationReleaseCollector, times(2)).fetchReleasesForUserAndFrequency(any(), anyInt());
      verify(notificationSenderSupplier, times(2)).apply(any());
      verify(notificationConfigRepository, times(2)).save(any());
    }

    @Test
    @DisplayName("nothing is called if notification is not due")
    void test_nothing_called_if_not_due() {
      // given
      notificationConfig.setLastNotificationDate(LocalDate.now());
      doReturn(List.of(notificationConfig)).when(notificationConfigRepository).findAllActive();

      // when
      underTest.notifyOnFrequency();

      // then
      verifyNoInteractions(notificationReleaseCollector);
    }
  }

  @Nested
  @DisplayName("Tests for notification on release date")
  class ReleaseDateTests {

    private final NotificationConfigEntity notificationConfig = NotificationConfigEntity.builder()
        .user(USER)
        .channel(EMAIL)
        .notificationAtReleaseDate(true)
        .notify(true)
        .build();

    @Test
    @DisplayName("repository is called")
    void test_repository_called() {
      // when
      underTest.notifyOnReleaseDate();

      // then
      verify(notificationConfigRepository).findAllActive();
    }

    @Test
    @DisplayName("releaseCollector is called")
    void test_release_collector_called() {
      // given
      doReturn(List.of(notificationConfig)).when(notificationConfigRepository).findAllActive();
      doReturn(mock(NotificationSender.class)).when(notificationSenderSupplier).apply(any());
      doReturn(Collections.emptyList()).when(notificationReleaseCollector).fetchTodaysReleaseForUser(any());

      // when
      underTest.notifyOnReleaseDate();

      // then
      verify(notificationReleaseCollector).fetchTodaysReleaseForUser(notificationConfig.getUser());
    }

    @Test
    @DisplayName("serviceSupplier is called")
    void test_service_supplier_called() {
      // given
      doReturn(List.of(notificationConfig)).when(notificationConfigRepository).findAllActive();
      doReturn(mock(NotificationSender.class)).when(notificationSenderSupplier).apply(any());
      doReturn(Collections.emptyList()).when(notificationReleaseCollector).fetchTodaysReleaseForUser(any());

      // when
      underTest.notifyOnReleaseDate();

      // then
      verify(notificationSenderSupplier).apply(notificationConfig.getChannel());
    }

    @Test
    @DisplayName("message is sent with notificationService")
    void test_message_sent() {
      // given
      var notificationServiceMock = mock(NotificationSender.class);
      var todaysReleases = List.of(ReleaseDtoFactory.createDefault());
      doReturn(List.of(notificationConfig)).when(notificationConfigRepository).findAllActive();
      doReturn(notificationServiceMock).when(notificationSenderSupplier).apply(any());
      doReturn(todaysReleases).when(notificationReleaseCollector).fetchTodaysReleaseForUser(any());

      // when
      underTest.notifyOnReleaseDate();

      // then
      verify(notificationServiceMock).sendReleaseDateMessage(notificationConfig.getUser(), todaysReleases);
    }

    @Test
    @DisplayName("services are called for each active config")
    void test_services_called_for_each_config() {
      // given
      var notificationConfig2 = NotificationConfigEntity.builder().user(USER).notify(true).notificationAtReleaseDate(true).channel(TELEGRAM).build();
      doReturn(List.of(notificationConfig, notificationConfig2)).when(notificationConfigRepository).findAllActive();
      doReturn(mock(NotificationSender.class)).when(notificationSenderSupplier).apply(any());
      doReturn(Collections.emptyList()).when(notificationReleaseCollector).fetchTodaysReleaseForUser(any());

      // when
      underTest.notifyOnReleaseDate();

      // then
      verify(notificationReleaseCollector, times(2)).fetchTodaysReleaseForUser(any());
      verify(notificationSenderSupplier, times(2)).apply(any());
    }

    @Test
    @DisplayName("nothing is called if notification on release date is not active")
    void test_nothing_is_called() {
      // given
      notificationConfig.setNotificationAtReleaseDate(false);
      doReturn(List.of(notificationConfig)).when(notificationConfigRepository).findAllActive();

      // when
      underTest.notifyOnReleaseDate();

      // then
      verifyNoInteractions(notificationReleaseCollector);
    }
  }

  @Nested
  @DisplayName("Tests for notification on announcement date")
  class AnnouncementDateTests {

    private final NotificationConfigEntity notificationConfig = NotificationConfigEntity.builder()
        .user(USER)
        .channel(EMAIL)
        .notificationAtAnnouncementDate(true)
        .notify(true)
        .build();

    @Test
    @DisplayName("repository is called")
    void test_repository_called() {
      // when
      underTest.notifyOnAnnouncementDate();

      // then
      verify(notificationConfigRepository).findAllActive();
    }

    @Test
    @DisplayName("releaseCollector is called")
    void test_release_collector_called() {
      // given
      doReturn(List.of(notificationConfig)).when(notificationConfigRepository).findAllActive();
      doReturn(mock(NotificationSender.class)).when(notificationSenderSupplier).apply(any());
      doReturn(Collections.emptyList()).when(notificationReleaseCollector).fetchTodaysAnnouncementsForUser(any());

      // when
      underTest.notifyOnAnnouncementDate();

      // then
      verify(notificationReleaseCollector).fetchTodaysAnnouncementsForUser(notificationConfig.getUser());
    }

    @Test
    @DisplayName("serviceSupplier is called")
    void test_service_supplier_called() {
      // given
      doReturn(List.of(notificationConfig)).when(notificationConfigRepository).findAllActive();
      doReturn(mock(NotificationSender.class)).when(notificationSenderSupplier).apply(any());
      doReturn(Collections.emptyList()).when(notificationReleaseCollector).fetchTodaysAnnouncementsForUser(any());

      // when
      underTest.notifyOnAnnouncementDate();

      // then
      verify(notificationSenderSupplier).apply(notificationConfig.getChannel());
    }

    @Test
    @DisplayName("message is sent with notificationService")
    void test_message_sent() {
      // given
      var notificationServiceMock = mock(NotificationSender.class);
      var todaysAnnouncements = List.of(ReleaseDtoFactory.createDefault());
      doReturn(List.of(notificationConfig)).when(notificationConfigRepository).findAllActive();
      doReturn(notificationServiceMock).when(notificationSenderSupplier).apply(any());
      doReturn(todaysAnnouncements).when(notificationReleaseCollector).fetchTodaysAnnouncementsForUser(any());

      // when
      underTest.notifyOnAnnouncementDate();

      // then
      verify(notificationServiceMock).sendAnnouncementDateMessage(notificationConfig.getUser(), todaysAnnouncements);
    }

    @Test
    @DisplayName("services are called for each active config")
    void test_services_called_for_each_config() {
      // given
      var notificationConfig2 = NotificationConfigEntity.builder().user(USER).notify(true).notificationAtAnnouncementDate(true).channel(TELEGRAM).build();
      doReturn(List.of(notificationConfig, notificationConfig2)).when(notificationConfigRepository).findAllActive();
      doReturn(mock(NotificationSender.class)).when(notificationSenderSupplier).apply(any());
      doReturn(Collections.emptyList()).when(notificationReleaseCollector).fetchTodaysAnnouncementsForUser(any());

      // when
      underTest.notifyOnAnnouncementDate();

      // then
      verify(notificationReleaseCollector, times(2)).fetchTodaysAnnouncementsForUser(any());
      verify(notificationSenderSupplier, times(2)).apply(any());
    }

    @Test
    @DisplayName("nothing is called if notification on announcement date is not active")
    void test_nothing_is_called() {
      // given
      notificationConfig.setNotificationAtAnnouncementDate(false);
      doReturn(List.of(notificationConfig)).when(notificationConfigRepository).findAllActive();

      // when
      underTest.notifyOnAnnouncementDate();

      // then
      verifyNoInteractions(notificationReleaseCollector);
    }
  }
}
