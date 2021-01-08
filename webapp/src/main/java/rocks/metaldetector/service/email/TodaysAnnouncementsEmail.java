package rocks.metaldetector.service.email;

import rocks.metaldetector.butler.facade.dto.ReleaseDto;
import rocks.metaldetector.config.constants.ViewNames;

import java.util.List;

public final class TodaysAnnouncementsEmail extends AbstractEmail {

  public static final String SUBJECT = "Your announcements";

  private final String recipient;
  private final String username;
  private final List<ReleaseDto> todaysAnnouncements;

  public TodaysAnnouncementsEmail(String recipient, String username, List<ReleaseDto> todaysAnnouncements) {
    this.recipient = recipient;
    this.username = username;
    this.todaysAnnouncements = todaysAnnouncements;
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
    return ViewNames.EmailTemplates.TODAYS_ANNOUNCEMENTS;
  }

  @Override
  void buildViewModel() {
    addViewModelEntry(ViewModelEntry.builder()
                          .name("username")
                          .value(username)
                          .build());

    addViewModelEntry(ViewModelEntry.builder()
                          .name("todaysAnnouncements")
                          .value(todaysAnnouncements)
                          .build());
  }
}
