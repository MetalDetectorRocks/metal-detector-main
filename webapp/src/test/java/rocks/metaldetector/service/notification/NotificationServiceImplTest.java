package rocks.metaldetector.service.notification;

import org.assertj.core.api.WithAssertions;
import org.assertj.core.data.TemporalUnitLessThanOffset;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
import rocks.metaldetector.service.user.UserDto;
import rocks.metaldetector.service.user.UserEntityFactory;
import rocks.metaldetector.service.user.UserService;
import rocks.metaldetector.support.TimeRange;
import rocks.metaldetector.support.exceptions.ResourceNotFoundException;
import rocks.metaldetector.testutil.DtoFactory.ArtistDtoFactory;
import rocks.metaldetector.testutil.DtoFactory.ReleaseDtoFactory;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static rocks.metaldetector.service.email.NewReleasesEmail.SUBJECT;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest implements WithAssertions {

  @Mock
  private ReleaseService releaseService;

  @Mock
  private UserService userService;

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

  @InjectMocks
  private NotificationServiceImpl underTest;

  @Captor
  private ArgumentCaptor<TimeRange> timeRangeCaptor;

  @Captor
  private ArgumentCaptor<AbstractEmail> emailCaptor;

  @Mock
  private UserDto user;

  @BeforeEach
  void setup() {
    when(user.getPublicId()).thenReturn("userId");
  }

  @AfterEach
  void tearDown() {
    reset(releaseService, userService, emailService, followArtistService, user, notificationConfigRepository, notificationConfigTransformer, currentUserSupplier);
  }

  @Test
  @DisplayName("FollowArtistService is called on notification")
  void follow_artist_service_is_called() {
    // given
    when(userService.getUserByPublicId(anyString())).thenReturn(user);

    // when
    underTest.notifyUser(user.getPublicId());

    // then
    verify(followArtistService).getFollowedArtistsOfUser(user.getPublicId());
  }

  @Test
  @DisplayName("UserService is called with correct id on notification")
  void notify_calls_user_service() {
    // given
    when(userService.getUserByPublicId(anyString())).thenReturn(user);

    // when
    underTest.notifyUser(user.getPublicId());

    // then
    verify(userService).getUserByPublicId(user.getPublicId());
  }

  @Test
  @DisplayName("ReleasesService is called on notification")
  void notify_calls_releases_service() {
    // given
    var artistDto = ArtistDtoFactory.createDefault();
    LocalDate now = LocalDate.now();
    TemporalUnitLessThanOffset offset = new TemporalUnitLessThanOffset(1, ChronoUnit.DAYS);
    when(releaseService.findAllReleases(any(), any())).thenReturn(Collections.emptyList());
    when(userService.getUserByPublicId(anyString())).thenReturn(user);
    when(followArtistService.getFollowedArtistsOfUser(any())).thenReturn(List.of(artistDto));

    // when
    underTest.notifyUser(user.getPublicId());

    // then
    verify(releaseService).findAllReleases(eq(List.of(artistDto.getArtistName())), timeRangeCaptor.capture());
    assertThat(timeRangeCaptor.getValue().getDateFrom()).isCloseTo(now, offset);
    assertThat(timeRangeCaptor.getValue().getDateTo()).isCloseTo(now.plusMonths(3), offset);
  }

  @Test
  @DisplayName("ReleasesService is not called if no artists exist")
  void notify_does_not_call_releases_service() {
    // given
    when(userService.getUserByPublicId(anyString())).thenReturn(user);

    // when
    underTest.notifyUser(user.getPublicId());

    // then
    verifyNoInteractions(releaseService);
  }

  @Test
  @DisplayName("EmailService is called on notification")
  void notify_calls_email_service() {
    // given
    var releaseDtos = List.of(ReleaseDtoFactory.createDefault());
    when(userService.getUserByPublicId(any())).thenReturn(user);
    when(releaseService.findAllReleases(any(), any())).thenReturn(releaseDtos);
    when(followArtistService.getFollowedArtistsOfUser(any())).thenReturn(List.of(ArtistDtoFactory.createDefault()));

    // when
    underTest.notifyUser(user.getPublicId());

    // then
    verify(emailService).sendEmail(any());
  }

  @Test
  @DisplayName("Correct email is sent on notification")
  void notify_sends_correct_email() {
    // given
    var releaseDtos = List.of(ReleaseDtoFactory.createDefault());
    when(userService.getUserByPublicId(any())).thenReturn(user);
    when(releaseService.findAllReleases(any(), any())).thenReturn(releaseDtos);
    when(followArtistService.getFollowedArtistsOfUser(any())).thenReturn(List.of(ArtistDtoFactory.createDefault()));

    // when
    underTest.notifyUser(user.getPublicId());

    // then
    verify(emailService).sendEmail(emailCaptor.capture());

    AbstractEmail email = emailCaptor.getValue();
    assertThat(email.getRecipient()).isEqualTo(user.getEmail());
    assertThat(email.getSubject()).isEqualTo(SUBJECT);
    assertThat(email.getTemplateName()).isEqualTo(ViewNames.EmailTemplates.NEW_RELEASES);

    List<ReleaseDto> releases = (List<ReleaseDto>) email.getEnhancedViewModel("dummy-base-url").get("newReleases");
    assertThat(releases).isEqualTo(releaseDtos);
  }

  @Test
  @DisplayName("EmailService not called if no releases exist")
  void notify_does_not_call_email_service() {
    // given
    when(userService.getUserByPublicId(any())).thenReturn(user);
    when(releaseService.findAllReleases(any(), any())).thenReturn(Collections.emptyList());

    // when
    underTest.notifyUser(user.getPublicId());

    // then
    verifyNoInteractions(emailService);
  }

  @Test
  @DisplayName("UserService is called on notification for all to get active users")
  void notify_all_calls_user_service() {
    // when
    underTest.notifyAllUsers();

    // then
    verify(userService).getAllActiveUsers();
  }

  @Test
  @DisplayName("All services are called times the number of active users on notification for all users")
  void notify_all_calls_all_services_for_each_user() {
    // given
    var userList = List.of(user, user);
    when(userService.getAllActiveUsers()).thenReturn(userList);
    when(userService.getUserByPublicId(any())).thenReturn(user);
    when(releaseService.findAllReleases(any(), any())).thenReturn(List.of(ReleaseDtoFactory.createDefault()));
    when(followArtistService.getFollowedArtistsOfUser(any())).thenReturn(List.of(ArtistDtoFactory.createDefault()));

    // when
    underTest.notifyAllUsers();

    // then
    verify(userService, times(userList.size())).getUserByPublicId(any());
    verify(releaseService, times(userList.size())).findAllReleases(any(), any());
    verify(emailService, times(userList.size())).sendEmail(any());
    verify(followArtistService, times(userList.size())).getFollowedArtistsOfUser(any());
  }

  @Test
  @DisplayName("Getting current user's config calls currentUserSupplier")
  void test_get_config_calls_current_user_supplier() {
    // given
    UserEntity userEntity = UserEntityFactory.createUser("name", "mail@mail.mail");
    doReturn(userEntity).when(currentUserSupplier).get();
    doReturn(Optional.of(NotificationConfigEntity.builder().user(userEntity).build())).when(notificationConfigRepository).findByUserId(any());

    // when
    underTest.getCurrentUserNotificationConfig();

    // then
    verify(currentUserSupplier).get();
  }

  @Test
  @DisplayName("Getting current user's config calls notificationConfigRepository")
  void test_get_config_calls_notification_repo() {
    // given
    var mockUser = mock(UserEntity.class);
    var userId = 666L;
    doReturn(userId).when(mockUser).getId();
    doReturn(mockUser).when(currentUserSupplier).get();
    doReturn(Optional.of(NotificationConfigEntity.builder().user(mockUser).build())).when(notificationConfigRepository).findByUserId(any());

    // when
    underTest.getCurrentUserNotificationConfig();

    // then
    verify(notificationConfigRepository).findByUserId(userId);
  }

  @Test
  @DisplayName("Getting current user's config throws exception when id not found")
  void test_get_config_throws_exception() {
    // given
    var mockUser = mock(UserEntity.class);
    var userId = 666L;
    var publicUserId = "123abc";
    doReturn(userId).when(mockUser).getId();
    doReturn(publicUserId).when(mockUser).getPublicId();
    doReturn(mockUser).when(currentUserSupplier).get();
    doReturn(Optional.empty()).when(notificationConfigRepository).findByUserId(any());

    // when
    var throwable = catchThrowable(() -> underTest.getCurrentUserNotificationConfig());

    // then
    assertThat(throwable).isInstanceOf(ResourceNotFoundException.class);
    assertThat(throwable).hasMessageContaining(publicUserId);
  }

  @Test
  @DisplayName("Getting current user's config calls notificationConfigTransformer")
  void test_get_config_calls_notification_config_trafo() {
    // given
    var userEntity = UserEntityFactory.createUser("name", "mail@mail.mail");
    var notificationConfigEntity = NotificationConfigEntity.builder().user(userEntity).build();
    doReturn(userEntity).when(currentUserSupplier).get();
    doReturn(Optional.of(notificationConfigEntity)).when(notificationConfigRepository).findByUserId(any());

    // when
    underTest.getCurrentUserNotificationConfig();

    // then
    verify(notificationConfigTransformer).transform(notificationConfigEntity);
  }

  @Test
  @DisplayName("Getting current user's config returns dto")
  void test_get_config_returns_dto() {
    // given
    var userEntity = UserEntityFactory.createUser("name", "mail@mail.mail");
    var notificationConfigDto = NotificationConfigDto.builder().frequencyInWeeks(4).build();
    doReturn(userEntity).when(currentUserSupplier).get();
    doReturn(Optional.of(NotificationConfigEntity.builder().user(userEntity).build())).when(notificationConfigRepository).findByUserId(any());
    doReturn(notificationConfigDto).when(notificationConfigTransformer).transform(any());

    // when
    var result = underTest.getCurrentUserNotificationConfig();

    // then
    assertThat(result).isEqualTo(notificationConfigDto);
  }

  @Test
  @DisplayName("Updating current user's config calls currentUserSupplier")
  void test_update_config_calls_current_user_supplier() {
    // given
    UserEntity userEntity = UserEntityFactory.createUser("name", "mail@mail.mail");
    doReturn(userEntity).when(currentUserSupplier).get();
    doReturn(Optional.of(NotificationConfigEntity.builder().user(userEntity).build())).when(notificationConfigRepository).findByUserId(any());

    // when
    underTest.updateCurrentUserNotificationConfig(new NotificationConfigDto());

    // then
    verify(currentUserSupplier).get();
  }

  @Test
  @DisplayName("Updating current user's config calls notificationConfigRepository")
  void test_update_config_calls_notification_repo() {
    // given
    var mockUser = mock(UserEntity.class);
    var userId = 666L;
    doReturn(userId).when(mockUser).getId();
    doReturn(mockUser).when(currentUserSupplier).get();
    doReturn(Optional.of(NotificationConfigEntity.builder().user(mockUser).build())).when(notificationConfigRepository).findByUserId(any());

    // when
    underTest.updateCurrentUserNotificationConfig(new NotificationConfigDto());

    // then
    verify(notificationConfigRepository).findByUserId(userId);
  }

  @Test
  @DisplayName("Updating current user's config throws exception when id not found")
  void test_update_config_throws_exception() {
    // given
    var mockUser = mock(UserEntity.class);
    var userId = 666L;
    var publicUserId = "123abc";
    doReturn(userId).when(mockUser).getId();
    doReturn(publicUserId).when(mockUser).getPublicId();
    doReturn(mockUser).when(currentUserSupplier).get();
    doReturn(Optional.empty()).when(notificationConfigRepository).findByUserId(any());

    // when
    var throwable = catchThrowable(() -> underTest.updateCurrentUserNotificationConfig(new NotificationConfigDto()));

    // then
    assertThat(throwable).isInstanceOf(ResourceNotFoundException.class);
    assertThat(throwable).hasMessageContaining(publicUserId);
  }

  @Test
  @DisplayName("Updated config is saved")
  void test_updated_config_saved() {
    // given
    ArgumentCaptor<NotificationConfigEntity> argumentCaptor = ArgumentCaptor.forClass(NotificationConfigEntity.class);
    var userEntity = UserEntityFactory.createUser("name", "mail@mail.mail");
    var notificationConfig = NotificationConfigEntity.builder().user(userEntity)
        .frequencyInWeeks(2)
        .build();
    var notificationConfigDto = NotificationConfigDto.builder()
        .frequencyInWeeks(4)
        .notify(true).build();
    doReturn(userEntity).when(currentUserSupplier).get();
    doReturn(Optional.of(notificationConfig)).when(notificationConfigRepository).findByUserId(any());

    // when
    underTest.updateCurrentUserNotificationConfig(notificationConfigDto);

    // then
    verify(notificationConfigRepository).save(argumentCaptor.capture());
    var savedEntity = argumentCaptor.getValue();
    assertThat(savedEntity.getUser()).isEqualTo(notificationConfig.getUser());
    assertThat(savedEntity.getFrequencyInWeeks()).isEqualTo(notificationConfigDto.getFrequencyInWeeks());
    assertThat(savedEntity.getNotify()).isEqualTo(notificationConfigDto.isNotify());
    assertThat(savedEntity.getNotificationAtReleaseDate()).isEqualTo(notificationConfigDto.isNotificationAtReleaseDate());
    assertThat(savedEntity.getNotificationAtAnnouncementDate()).isEqualTo(notificationConfigDto.isNotificationAtAnnouncementDate());
  }
}
