package rocks.metaldetector.service.notification;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import rocks.metaldetector.butler.facade.ReleaseService;
import rocks.metaldetector.butler.facade.dto.ReleaseDto;
import rocks.metaldetector.service.artist.ArtistDto;
import rocks.metaldetector.service.artist.ArtistsService;
import rocks.metaldetector.service.email.EmailService;
import rocks.metaldetector.service.email.NewReleasesEmail;
import rocks.metaldetector.service.user.UserDto;
import rocks.metaldetector.service.user.UserService;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class NotificationServiceImpl implements NotificationService {

  private final ReleaseService releaseService;
  private final ArtistsService artistsService;
  private final UserService userService;
  private final EmailService emailService;

  @Override
  public void notifyAllUsers() {
    userService.getAllActiveUsers().forEach(user -> notifyUser(user.getPublicId()));
  }

  @Override
  public void notifyUser(String publicUserId) {
    UserDto user = userService.getUserByPublicId(publicUserId);
    List<String> followedArtistsNames = artistsService.findFollowedArtistsPerUser(publicUserId)
        .stream().map(ArtistDto::getArtistName).collect(Collectors.toList());
    List<ReleaseDto> newReleases = releaseService.findReleases(followedArtistsNames, LocalDate.now(), LocalDate.now().plusMonths(3));
    emailService.sendEmail(new NewReleasesEmail(user.getEmail(), user.getUsername(), newReleases));
  }
}
