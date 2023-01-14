package rocks.metaldetector.service.email;

import java.util.List;

import static rocks.metaldetector.config.constants.ViewNames.EmailTemplates.ACCOUNT_DELETED;

public final class AccountDeletedEmail implements Email {

  public static final String SUBJECT = "Account deleted";

  private final String recipient;
  private final String username;

  public AccountDeletedEmail(String recipient, String username) {
    this.recipient = recipient;
    this.username = username;
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
    return ACCOUNT_DELETED;
  }

  @Override
  public List<ViewModelEntry> getViewModelEntries() {
    return List.of(
        new ViewModelEntry("username", username)
    );
  }
}
