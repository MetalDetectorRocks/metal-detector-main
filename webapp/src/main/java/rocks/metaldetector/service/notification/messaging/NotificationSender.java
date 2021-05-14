package rocks.metaldetector.service.notification.messaging;

import rocks.metaldetector.butler.facade.dto.ReleaseDto;
import rocks.metaldetector.persistence.domain.user.AbstractUserEntity;

import java.util.List;

public interface NotificationSender {

  void sendFrequencyMessage(AbstractUserEntity user, List<ReleaseDto> upcomingReleases, List<ReleaseDto> recentReleases);
  void sendReleaseDateMessage(AbstractUserEntity user, List<ReleaseDto> todaysReleases);
  void sendAnnouncementDateMessage(AbstractUserEntity user, List<ReleaseDto> todaysAnnouncements);
}
