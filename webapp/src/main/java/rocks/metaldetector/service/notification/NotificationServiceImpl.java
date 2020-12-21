package rocks.metaldetector.service.notification;

import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import rocks.metaldetector.butler.facade.ReleaseService;
import rocks.metaldetector.butler.facade.dto.ReleaseDto;
import rocks.metaldetector.persistence.domain.notification.NotificationConfigEntity;
import rocks.metaldetector.persistence.domain.notification.NotificationConfigRepository;
import rocks.metaldetector.persistence.domain.user.UserEntity;
import rocks.metaldetector.security.CurrentUserSupplier;
import rocks.metaldetector.service.artist.ArtistDto;
import rocks.metaldetector.service.artist.FollowArtistService;
import rocks.metaldetector.service.email.EmailService;
import rocks.metaldetector.service.email.NewReleasesEmail;
import rocks.metaldetector.service.user.UserDto;
import rocks.metaldetector.service.user.UserService;
import rocks.metaldetector.support.TimeRange;
import rocks.metaldetector.support.exceptions.ResourceNotFoundException;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class NotificationServiceImpl implements NotificationService {

  private final ReleaseService releaseService;
  private final UserService userService;
  private final EmailService emailService;
  private final FollowArtistService followArtistService;
  private final NotificationConfigRepository notificationConfigRepository;
  private final NotificationConfigTransformer notificationConfigTransformer;
  private final CurrentUserSupplier currentUserSupplier;

  @Override
  @Scheduled(cron = "0 0 4 * * SUN")
  public void notifyAllUsers() {
    userService.getAllActiveUsers().forEach(user -> notifyUser(user.getPublicId()));
  }

  @Override
  public void notifyUser(String publicUserId) {
    UserDto user = userService.getUserByPublicId(publicUserId);
    List<String> followedArtistsNames = followArtistService.getFollowedArtistsOfUser(publicUserId).stream()
        .map(ArtistDto::getArtistName).collect(Collectors.toList());

    if (!followedArtistsNames.isEmpty()) {
      var now = LocalDate.now();
      List<ReleaseDto> newReleases = releaseService.findAllReleases(followedArtistsNames, new TimeRange(now, now.plusMonths(3)));

      if (!newReleases.isEmpty()) {
        emailService.sendEmail(new NewReleasesEmail(user.getEmail(), user.getUsername(), newReleases));
      }
    }
  }

  @Override
  public NotificationConfigDto getCurrentUserNotificationConfig() {
    UserEntity currentUser = currentUserSupplier.get();
    NotificationConfigEntity notificationConfigEntity = notificationConfigRepository.findByUserId(currentUser.getId())
        .orElseThrow(() -> new ResourceNotFoundException("Notification config for user '" + currentUser.getPublicId() + "' not found"));
    return notificationConfigTransformer.transform(notificationConfigEntity);
  }

  @Override
  public void updateCurrentUserNotificationConfig(NotificationConfigDto notificationConfigDto) {
    UserEntity currentUser = currentUserSupplier.get();
    NotificationConfigEntity notificationConfigEntity = notificationConfigRepository.findByUserId(currentUser.getId())
        .orElseThrow(() -> new ResourceNotFoundException("Notification config for user '" + currentUser.getPublicId() + "' not found"));

    notificationConfigEntity.setFrequencyInWeeks(notificationConfigDto.getFrequencyInWeeks());
    notificationConfigEntity.setNotificationAtAnnouncementDate(notificationConfigDto.isNotificationAtAnnouncementDate());
    notificationConfigEntity.setNotificationAtReleaseDate(notificationConfigDto.isNotificationAtReleaseDate());
    notificationConfigEntity.setNotify(notificationConfigDto.isNotify());

    notificationConfigRepository.save(notificationConfigEntity);
  }
}
