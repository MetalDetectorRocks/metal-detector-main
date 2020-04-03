package rocks.metaldetector.service.email;

import lombok.EqualsAndHashCode;
import rocks.metaldetector.butler.facade.dto.ReleaseDto;
import rocks.metaldetector.config.constants.ViewNames;

import java.util.List;

@EqualsAndHashCode(callSuper = false)
public final class NewReleasesEmail extends AbstractEmail {

  private static final String SUBJECT = "Your new releases";

  private final String recipient;
  private final String username;
  private final List<ReleaseDto> newReleases;

  public NewReleasesEmail(String recipient, String username, List<ReleaseDto> newReleases) {
    this.recipient = recipient;
    this.username = username;
    this.newReleases = newReleases;
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
                          .name("newReleases")
                          .value(newReleases)
                          .build());
  }
}
