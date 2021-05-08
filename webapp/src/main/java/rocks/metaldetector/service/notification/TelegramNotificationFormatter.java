package rocks.metaldetector.service.notification;

import org.springframework.stereotype.Component;
import rocks.metaldetector.butler.facade.dto.ReleaseDto;

import java.util.List;

@Component
public class TelegramNotificationFormatter {

  public String formatFrequencyNotificationMessage(List<ReleaseDto> upcomingReleases, List<ReleaseDto> recentReleases) {
    StringBuilder stringBuilder = new StringBuilder("Your new metal releases!\n");
    stringBuilder.append("The following releases have been released recently or will be released in the next days:\n\n");
    recentReleases.forEach(releaseDto ->
                               stringBuilder.append("  - ")
                                   .append(releaseDto.getArtist())
                                   .append(" - ")
                                   .append(releaseDto.getAlbumTitle())
                                   .append(" - ")
                                   .append(releaseDto.getReleaseDateAsDisplayString())
                                   .append("\n"));
    stringBuilder.append("\n");
    upcomingReleases.forEach(releaseDto ->
                                 stringBuilder.append("  - ")
                                     .append(releaseDto.getArtist())
                                     .append(" - ")
                                     .append(releaseDto.getAlbumTitle())
                                     .append(" - ")
                                     .append(releaseDto.getReleaseDateAsDisplayString())
                                     .append("\n"));
    return stringBuilder.toString();
  }

  public String formatDateNotificationMessage(List<ReleaseDto> releases, String message) {
    StringBuilder stringBuilder = new StringBuilder(message + "\n");
    releases.forEach(releaseDto ->
                         stringBuilder.append("  - ")
                             .append(releaseDto.getArtist())
                             .append(" - ")
                             .append(releaseDto.getAlbumTitle())
                             .append(" - ")
                             .append(releaseDto.getReleaseDateAsDisplayString())
                             .append("\n"));
    return stringBuilder.toString();
  }
}
