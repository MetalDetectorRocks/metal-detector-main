package rocks.metaldetector.service.email;

import rocks.metaldetector.butler.facade.dto.ReleaseDto;
import rocks.metaldetector.config.constants.ViewNames;

import java.util.List;

public final class TodaysReleasesEmail extends AbstractEmail {

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
    return ViewNames.EmailTemplates.TODAYS_RELEASES;
  }

  @Override
  void buildViewModel() {
    addViewModelEntry(ViewModelEntry.builder()
                          .name("username")
                          .value(username)
                          .build());

    addViewModelEntry(ViewModelEntry.builder()
                          .name("todaysReleases")
                          .value(todaysReleases)
                          .build());
  }
}
