package rocks.metaldetector.service.email;

import rocks.metaldetector.butler.facade.dto.ReleaseDto;

import java.util.List;

import static rocks.metaldetector.service.email.EmailTemplateNames.NEW_RELEASES;

public final class ReleasesEmail implements Email {

  public static final String SUBJECT = "Your latest release updates";

  private final String recipient;
  private final String username;
  private final List<ReleaseDto> upcomingReleases;
  private final List<ReleaseDto> recentReleases;

  public ReleasesEmail(String recipient, String username, List<ReleaseDto> upcomingReleases, List<ReleaseDto> recentReleases) {
    this.recipient = recipient;
    this.username = username;
    this.upcomingReleases = upcomingReleases;
    this.recentReleases = recentReleases;
  }

  @Override
  public String getRecipient() {
    return recipient;
  }

  @Override
  public String getSubject() {
    return SUBJECT;
  }

  @Override
  public String getTemplateName() {
    return NEW_RELEASES;
  }

  @Override
  public List<ViewModelEntry> getViewModelEntries() {
    return List.of(
        new ViewModelEntry("username", username),
        new ViewModelEntry("upcomingReleases", upcomingReleases),
        new ViewModelEntry("recentReleases", recentReleases)
    );
  }
}
