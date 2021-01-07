package rocks.metaldetector.service.notification;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rocks.metaldetector.butler.facade.ReleaseService;
import rocks.metaldetector.butler.facade.dto.ReleaseDto;
import rocks.metaldetector.persistence.domain.notification.NotificationConfigEntity;
import rocks.metaldetector.persistence.domain.notification.NotificationConfigRepository;
import rocks.metaldetector.persistence.domain.user.UserEntity;
import rocks.metaldetector.security.CurrentUserSupplier;
import rocks.metaldetector.service.artist.ArtistDto;
import rocks.metaldetector.service.artist.FollowArtistService;
import rocks.metaldetector.service.email.EmailService;
import rocks.metaldetector.service.email.ReleasesEmail;
import rocks.metaldetector.support.TimeRange;
import rocks.metaldetector.support.exceptions.ResourceNotFoundException;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static java.time.temporal.ChronoUnit.WEEKS;

@Service
@AllArgsConstructor
public class NotificationServiceImpl implements NotificationService {

  private final ReleaseService releaseService;
  private final EmailService emailService;
  private final FollowArtistService followArtistService;
  private final NotificationConfigRepository notificationConfigRepository;
  private final NotificationConfigTransformer notificationConfigTransformer;
  private final CurrentUserSupplier currentUserSupplier;

  @Override
//  @Scheduled(cron = "0 0 4 * * SUN")
  @Transactional(readOnly = true)
  public void notifyAllUsers() {
    notificationConfigRepository.findAll().stream()
        .filter(config -> config.getUser().isEnabled() &&
                          config.getNotify())
        .forEach(this::notify);
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

  private void notify(NotificationConfigEntity notificationConfigEntity) {
    var now = LocalDate.now();
    boolean shouldNotify = notificationConfigEntity.getLastNotificationDate() == null ||
                           WEEKS.between(notificationConfigEntity.getLastNotificationDate(), now) >= notificationConfigEntity.getFrequencyInWeeks();

    if (shouldNotify) {
      UserEntity user = notificationConfigEntity.getUser();
      List<String> followedArtistsNames = followArtistService.getFollowedArtistsOfUser(user.getPublicId()).stream()
          .map(ArtistDto::getArtistName).collect(Collectors.toList());

      if (!followedArtistsNames.isEmpty()) {
        List<ReleaseDto> upcomingReleases = releaseService.findAllReleases(followedArtistsNames, new TimeRange(now, now.plusWeeks(notificationConfigEntity.getFrequencyInWeeks())));
        List<ReleaseDto> recentReleases = releaseService.findAllReleases(followedArtistsNames, new TimeRange(now.minusWeeks(notificationConfigEntity.getFrequencyInWeeks()), now.minusDays(1)));

        if (!(upcomingReleases.isEmpty() && recentReleases.isEmpty())) {
          emailService.sendEmail(new ReleasesEmail(user.getEmail(), user.getUsername(), upcomingReleases, recentReleases));

          notificationConfigEntity.setLastNotificationDate(now);
          notificationConfigRepository.save(notificationConfigEntity);
        }
      }
    }
  }
}
