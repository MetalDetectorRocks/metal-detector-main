package rocks.metaldetector.service.notification.messaging;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import rocks.metaldetector.butler.facade.dto.ReleaseDto;
import rocks.metaldetector.persistence.domain.user.AbstractUserEntity;
import rocks.metaldetector.service.email.EmailService;
import rocks.metaldetector.service.email.ReleasesEmail;
import rocks.metaldetector.service.email.TodaysAnnouncementsEmail;
import rocks.metaldetector.service.email.TodaysReleasesEmail;

import java.util.List;

@Service
@AllArgsConstructor
public class EmailNotificationSender implements NotificationSender {

  private final EmailService emailService;

  @Override
  public void sendFrequencyMessage(AbstractUserEntity user, List<ReleaseDto> upcomingReleases, List<ReleaseDto> recentReleases) {
    emailService.sendEmail(new ReleasesEmail(user.getEmail(), user.getUsername(), upcomingReleases, recentReleases));
  }

  @Override
  public void sendReleaseDateMessage(AbstractUserEntity user, List<ReleaseDto> todaysReleases) {
    emailService.sendEmail(new TodaysReleasesEmail(user.getEmail(), user.getUsername(), todaysReleases));
  }

  @Override
  public void sendAnnouncementDateMessage(AbstractUserEntity user, List<ReleaseDto> todaysAnnouncements) {
    emailService.sendEmail(new TodaysAnnouncementsEmail(user.getEmail(), user.getUsername(), todaysAnnouncements));
  }
}
