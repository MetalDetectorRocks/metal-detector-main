package rocks.metaldetector.service.notification;

import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rocks.metaldetector.butler.facade.ReleaseService;
import rocks.metaldetector.butler.facade.dto.ReleaseDto;
import rocks.metaldetector.persistence.domain.notification.NotificationConfigEntity;
import rocks.metaldetector.persistence.domain.notification.NotificationConfigRepository;
import rocks.metaldetector.persistence.domain.user.AbstractUserEntity;
import rocks.metaldetector.service.artist.ArtistDto;
import rocks.metaldetector.service.artist.FollowArtistService;
import rocks.metaldetector.service.email.AbstractEmail;
import rocks.metaldetector.service.email.EmailService;
import rocks.metaldetector.service.email.ReleasesEmail;
import rocks.metaldetector.service.email.TodaysAnnouncementsEmail;
import rocks.metaldetector.service.email.TodaysReleasesEmail;
import rocks.metaldetector.support.TimeRange;
import rocks.metaldetector.telegram.facade.TelegramService;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import static java.time.temporal.ChronoUnit.WEEKS;

@Service
@AllArgsConstructor
public class NotificationServiceImpl implements NotificationService {

  static final List<Integer> SUPPORTED_FREQUENCIES = List.of(2, 4);
  static final String TODAYS_RELEASES_TEXT = "Today's metal releases:";
  static final String TODAYS_ANNOUNCEMENTS_TEXT = "Today's metal release announcements:";

  private final ReleaseService releaseService;
  private final EmailService emailService;
  private final FollowArtistService followArtistService;
  private final NotificationConfigRepository notificationConfigRepository;
  private final TelegramService telegramService;
  private final TelegramNotificationFormatter notificationFormatter;

  @Override
  @Scheduled(cron = "0 0 4 * * SUN")
  @Transactional
  public void notifyOnFrequency() {
    Map<Integer, ReleaseContainer> releaseContainer = createReleaseContainer();

    notificationConfigRepository.findAll().stream()
        .filter(config -> config.getUser().isEnabled() &&
                          config.getNotify())
        .forEach(config -> frequencyNotification(config, releaseContainer.get(config.getFrequencyInWeeks())));
  }

  @Override
  @Scheduled(cron = "0 0 7 * * *")
  @Transactional(readOnly = true)
  public void notifyOnReleaseDate() {
    var now = LocalDate.now();
    List<ReleaseDto> todaysReleases = releaseService.findAllReleases(Collections.emptyList(), new TimeRange(now, now));

    notificationConfigRepository.findAll().stream()
        .filter(config -> config.getUser().isEnabled() &&
                          config.getNotificationAtReleaseDate())
        .forEach(notificationConfig -> notifyOnSpecificDate(notificationConfig, todaysReleases, (AbstractUserEntity user, List<ReleaseDto> filteredReleases) ->
            new TodaysReleasesEmail(user.getEmail(), user.getUsername(), filteredReleases)));
  }

  @Override
  @Scheduled(cron = "0 0 7 * * *")
  @Transactional(readOnly = true)
  public void notifyOnAnnouncementDate() {
    var now = LocalDate.now();
    List<ReleaseDto> todaysAnnouncedReleases = releaseService.findAllReleases(Collections.emptyList(), new TimeRange(now, null)).stream()
        .filter(release -> release.getAnnouncementDate().isEqual(now))
        .collect(Collectors.toList());

    notificationConfigRepository.findAll().stream()
        .filter(config -> config.getUser().isEnabled() &&
                          config.getNotificationAtAnnouncementDate())
        .forEach(notificationConfig -> notifyOnSpecificDate(notificationConfig, todaysAnnouncedReleases, (AbstractUserEntity user, List<ReleaseDto> filteredReleases) ->
            new TodaysAnnouncementsEmail(user.getEmail(), user.getUsername(), filteredReleases)));
  }

  private void frequencyNotification(NotificationConfigEntity notificationConfig, ReleaseContainer releaseContainer) {
    var now = LocalDate.now();
    boolean shouldNotify = notificationConfig.getLastNotificationDate() == null ||
                           WEEKS.between(notificationConfig.getLastNotificationDate(), now) >= notificationConfig.getFrequencyInWeeks();

    if (shouldNotify) {
      AbstractUserEntity user = notificationConfig.getUser();
      List<String> followedArtistsNames = followArtistService.getFollowedArtistsOfUser(user.getPublicId()).stream()
          .map(ArtistDto::getArtistName).collect(Collectors.toList());

      if (!followedArtistsNames.isEmpty()) {
        List<ReleaseDto> upcomingReleases = releaseContainer.upcomingReleases.stream().filter(release -> followedArtistsNames.contains(release.getArtist())).collect(Collectors.toList());
        List<ReleaseDto> recentReleases = releaseContainer.recentReleases.stream().filter(release -> followedArtistsNames.contains(release.getArtist())).collect(Collectors.toList());

        if (!(upcomingReleases.isEmpty() && recentReleases.isEmpty())) {
          emailService.sendEmail(new ReleasesEmail(user.getEmail(), user.getUsername(), upcomingReleases, recentReleases));

          if (notificationConfig.getTelegramChatId() != null) {
            String message = notificationFormatter.formatFrequencyNotificationMessage(upcomingReleases, recentReleases);
            telegramService.sendMessage(notificationConfig.getTelegramChatId(), message);
          }

          notificationConfig.setLastNotificationDate(now);
          notificationConfigRepository.save(notificationConfig);
        }
      }
    }
  }

  private void notifyOnSpecificDate(NotificationConfigEntity notificationConfig, List<ReleaseDto> releases, BiFunction<AbstractUserEntity, List<ReleaseDto>, AbstractEmail> emailBiFunction) {
    AbstractUserEntity user = notificationConfig.getUser();
    List<String> followedArtistsNames = followArtistService.getFollowedArtistsOfUser(user.getPublicId()).stream()
        .map(ArtistDto::getArtistName).collect(Collectors.toList());

    if (!followedArtistsNames.isEmpty()) {
      List<ReleaseDto> filteredReleases = releases.stream().filter(release -> followedArtistsNames.contains(release.getArtist())).collect(Collectors.toList());

      if (!filteredReleases.isEmpty()) {
        AbstractEmail email = emailBiFunction.apply(user, filteredReleases);
        emailService.sendEmail(email);

        if (notificationConfig.getTelegramChatId() != null) {
          String text = email instanceof TodaysReleasesEmail ? TODAYS_RELEASES_TEXT : TODAYS_ANNOUNCEMENTS_TEXT;
          String message = notificationFormatter.formatDateNotificationMessage(filteredReleases, text);
          telegramService.sendMessage(notificationConfig.getTelegramChatId(), message);
        }
      }
    }
  }

  private Map<Integer, ReleaseContainer> createReleaseContainer() {
    var now = LocalDate.now();
    Map<Integer, ReleaseContainer> releaseContainers = new HashMap<>();

    for (int frequency : SUPPORTED_FREQUENCIES) {
      List<ReleaseDto> upcomingReleases = releaseService.findAllReleases(Collections.emptyList(), new TimeRange(now, now.plusWeeks(frequency)));
      List<ReleaseDto> recentReleases = releaseService.findAllReleases(Collections.emptyList(), new TimeRange(now.minusWeeks(frequency), now.minusDays(1)));
      releaseContainers.put(frequency, new ReleaseContainer(upcomingReleases, recentReleases));
    }

    return releaseContainers;
  }

  @AllArgsConstructor
  private static class ReleaseContainer {

    private final List<ReleaseDto> upcomingReleases;
    private final List<ReleaseDto> recentReleases;
  }
}
