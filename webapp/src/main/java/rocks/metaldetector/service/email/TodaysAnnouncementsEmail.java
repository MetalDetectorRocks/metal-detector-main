package rocks.metaldetector.service.email;

import rocks.metaldetector.butler.facade.dto.ReleaseDto;

import java.util.List;

import static rocks.metaldetector.config.constants.ViewNames.EmailTemplates.TODAYS_ANNOUNCEMENTS;

public final class TodaysAnnouncementsEmail implements Email {

  public static final String SUBJECT = "Today's metal album announcements";

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
    return TODAYS_ANNOUNCEMENTS;
  }

  @Override
  public List<ViewModelEntry> getViewModelEntries() {
    return List.of(
        new ViewModelEntry("username", username),
        new ViewModelEntry("todaysAnnouncements", todaysAnnouncements)
    );
  }
}
