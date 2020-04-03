package rocks.metaldetector.service.notification;

import org.assertj.core.api.WithAssertions;
import org.assertj.core.data.TemporalUnitLessThanOffset;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rocks.metaldetector.butler.facade.ReleaseService;
import rocks.metaldetector.service.artist.ArtistsService;
import rocks.metaldetector.service.email.EmailService;
import rocks.metaldetector.service.email.NewReleasesEmail;
import rocks.metaldetector.service.user.UserDto;
import rocks.metaldetector.service.user.UserService;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static rocks.metaldetector.testutil.DtoFactory.ArtistDtoFactory;
import static rocks.metaldetector.testutil.DtoFactory.ReleaseDtoFactory;
import static rocks.metaldetector.testutil.DtoFactory.UserDtoFactory;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest implements WithAssertions {

  @Mock
  private ReleaseService releaseService;

  @Mock
  private ArtistsService artistsService;

  @Mock
  private UserService userService;

  @Mock
  private EmailService emailService;

  @InjectMocks
  private NotificationServiceImpl underTest;

  @Captor
  private ArgumentCaptor<LocalDate> dateFromCaptor;

  @Captor
  private ArgumentCaptor<LocalDate> dateToCaptor;

  private UserDto user = UserDtoFactory.createDefault();

  @AfterEach
  void tearDown() {
    reset(releaseService, artistsService, userService, emailService);
  }

  @Test
  @DisplayName("UserService is called with correct id on notification")
  void notify_calls_user_service() {
    // when
    underTest.notifyUser(user.getPublicId());

    // then
    verify(userService, times(1)).getUserByPublicId(user.getPublicId());
  }

  @Test
  @DisplayName("ArtistsService is called on notification")
  void notify_calls_artists_service() {
    // when
    underTest.notifyUser(user.getPublicId());

    // then
    verify(artistsService, times(1)).findFollowedArtistsPerUser(user.getPublicId());
  }

  @Test
  @DisplayName("ReleasesService is called on notification")
  void notify_calls_releases_service() {
    // given
    var artistDto = ArtistDtoFactory.createDefault();
    LocalDate now = LocalDate.now();
    TemporalUnitLessThanOffset offset = new TemporalUnitLessThanOffset(1, ChronoUnit.DAYS);
    when(artistsService.findFollowedArtistsPerUser(any())).thenReturn(List.of(artistDto));
    when(releaseService.findReleases(any(), any(), any())).thenReturn(Collections.emptyList());

    // when
    underTest.notifyUser(user.getPublicId());

    // then
    verify(releaseService, times(1)).findReleases(eq(List.of(artistDto.getArtistName())), dateFromCaptor.capture(), dateToCaptor.capture());
    assertThat(dateFromCaptor.getValue()).isCloseTo(now, offset);
    assertThat(dateToCaptor.getValue()).isCloseTo(now.plusMonths(3), offset);
  }

  @Test
  @DisplayName("ReleasesService is not called if no artists exist")
  void notify_does_not_call_releases_service() {
    // given
    when(artistsService.findFollowedArtistsPerUser(any())).thenReturn(Collections.emptyList());

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
    when(artistsService.findFollowedArtistsPerUser(any())).thenReturn(List.of(ArtistDtoFactory.createDefault()));
    when(releaseService.findReleases(any(), any(), any())).thenReturn(releaseDtos);

    // when
    underTest.notifyUser(user.getPublicId());

    // then
    verify(emailService, times(1)).sendEmail(new NewReleasesEmail(user.getEmail(), user.getUsername(), releaseDtos));
  }

  @Test
  @DisplayName("EmailService not called if no releases exist")
  void notify_does_not_call_email_service() {
    // given
    when(userService.getUserByPublicId(any())).thenReturn(user);
    when(artistsService.findFollowedArtistsPerUser(any())).thenReturn(List.of(ArtistDtoFactory.createDefault()));
    when(releaseService.findReleases(any(), any(), any())).thenReturn(Collections.emptyList());

    // when
    underTest.notifyUser(user.getPublicId());

    // then
    verifyNoInteractions(emailService);
  }

  @Test
  @DisplayName("UserService is called on notification for all to get active users")
  void notifify_all_calls_user_service() {
    // when
    underTest.notifyAllUsers();

    // then
    verify(userService, times(1)).getAllActiveUsers();
  }

  @Test
  @DisplayName("All services are called times the number of active users on notification for all users")
  void notifify_all_calls_all_services_for_each_user() {
    // given
    var userList = List.of(user, user);
    when(userService.getAllActiveUsers()).thenReturn(userList);
    when(userService.getUserByPublicId(any())).thenReturn(user);
    when(artistsService.findFollowedArtistsPerUser(any())).thenReturn(List.of(ArtistDtoFactory.createDefault()));
    when(releaseService.findReleases(any(), any(), any())).thenReturn(List.of(ReleaseDtoFactory.createDefault()));

    // when
    underTest.notifyAllUsers();

    // then
    verify(userService, times(userList.size())).getUserByPublicId(any());
    verify(artistsService, times(userList.size())).findFollowedArtistsPerUser(any());
    verify(releaseService, times(userList.size())).findReleases(any(), any(), any());
    verify(emailService, times(userList.size())).sendEmail(any());
  }
}