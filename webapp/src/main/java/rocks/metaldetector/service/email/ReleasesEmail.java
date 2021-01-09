package rocks.metaldetector.service.email;

import rocks.metaldetector.butler.facade.dto.ReleaseDto;
import rocks.metaldetector.config.constants.ViewNames;

import java.util.List;

public final class ReleasesEmail extends AbstractEmail {

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
    return ViewNames.EmailTemplates.NEW_RELEASES;
  }

  @Override
  void buildViewModel() {
    addViewModelEntry(ViewModelEntry.builder()
                          .name("username")
                          .value(username)
                          .build());

    addViewModelEntry(ViewModelEntry.builder()
                          .name("upcomingReleases")
                          .value(upcomingReleases)
                          .build());

    addViewModelEntry(ViewModelEntry.builder()
                          .name("recentReleases")
                          .value(recentReleases)
                          .build());
  }
}
