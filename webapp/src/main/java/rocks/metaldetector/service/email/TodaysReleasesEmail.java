package rocks.metaldetector.service.email;

import rocks.metaldetector.butler.facade.dto.ReleaseDto;

import java.util.List;

import static rocks.metaldetector.config.constants.ViewNames.EmailTemplates.TODAYS_RELEASES;

public final class TodaysReleasesEmail implements Email {

  public static final String SUBJECT = "Today's releases";

  private final String recipient;
  private final String username;
  private final List<ReleaseDto> todaysReleases;

  public TodaysReleasesEmail(String recipient, String username, List<ReleaseDto> todaysReleases) {
    this.recipient = recipient;
    this.username = username;
    this.todaysReleases = todaysReleases;
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
    return TODAYS_RELEASES;
  }

  @Override
  public List<ViewModelEntry> getViewModelEntries() {
    return List.of(
        new ViewModelEntry("username", username),
        new ViewModelEntry("todaysReleases", todaysReleases)
    );
  }
}
