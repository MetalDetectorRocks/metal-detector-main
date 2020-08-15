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
import rocks.metaldetector.service.artist.FollowArtistService;
import rocks.metaldetector.service.email.AbstractEmail;
import rocks.metaldetector.service.email.EmailService;
import rocks.metaldetector.service.user.UserDto;
import rocks.metaldetector.service.user.UserService;
import rocks.metaldetector.support.TimeRange;
import rocks.metaldetector.testutil.DtoFactory.ArtistDtoFactory;
import rocks.metaldetector.testutil.DtoFactory.ReleaseDtoFactory;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static rocks.metaldetector.service.email.NewReleasesEmail.SUBJECT;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest implements WithAssertions {

  @Mock
  private ReleaseService releaseService;

  @Mock
  private UserService userService;

  @Mock
  private EmailService emailService;

  @Mock
  private FollowArtistService followArtistService;

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
    reset(releaseService, userService, emailService, followArtistService, user);
  }

  @Test
  @DisplayName("FollowArtistService is called on notification")
  void follow_artist_service_is_called() {
    // given
    when(userService.getUserByPublicId(anyString())).thenReturn(user);

    // when
    underTest.notifyUser(user.getPublicId());

    // then
    verify(followArtistService, times(1)).getFollowedArtistsOfUser(user.getPublicId());
  }

  @Test
  @DisplayName("UserService is called with correct id on notification")
  void notify_calls_user_service() {
    // given
    when(userService.getUserByPublicId(anyString())).thenReturn(user);

    // when
    underTest.notifyUser(user.getPublicId());

    // then
    verify(userService, times(1)).getUserByPublicId(user.getPublicId());
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
    verify(releaseService, times(1)).findAllReleases(eq(List.of(artistDto.getArtistName())), timeRangeCaptor.capture());
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
    verify(emailService, times(1)).sendEmail(any());
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
    verify(emailService, times(1)).sendEmail(emailCaptor.capture());

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
    verify(userService, times(1)).getAllActiveUsers();
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
}
